package com.jiayi.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.jiayi.entity.PmsProduct;
import com.jiayi.mapper.PmsProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class ShareService {

    private static final Logger log = LoggerFactory.getLogger(ShareService.class);

    private final PmsProductMapper productMapper;
    private final WechatService wechatService;
    private final File posterDir;
    private final String baseUrl;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public ShareService(PmsProductMapper productMapper, WechatService wechatService,
                        @Value("${app.upload-dir:./uploads}") String uploadDir) {
        this.productMapper = productMapper;
        this.wechatService = wechatService;
        File dir = new File(uploadDir);
        if (!dir.isAbsolute()) {
            dir = new File(System.getProperty("user.dir"), uploadDir);
        }
        this.posterDir = new File(dir, "posters");
        this.posterDir.mkdirs();
        this.baseUrl = "http://localhost:8080";
        log.info("海报目录: {}", this.posterDir.getAbsolutePath());
    }

    public String generatePoster(Long productId) {
        PmsProduct product = productMapper.selectById(productId);
        if (product == null) throw new RuntimeException("商品不存在");
        log.info("生成海报 productId={}, name={}, descriptionText=|{}|, description=|{}|",
                productId, product.getName(), product.getDescriptionText(),
                product.getDescription() != null ? product.getDescription().substring(0, Math.min(50, product.getDescription().length())) : null);

        int W = 500, H = 720;
        BufferedImage poster = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = poster.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Background
        g.setColor(new Color(0xF5F0EB));
        g.fillRect(0, 0, W, H);

        // Product image — top 50%, no text overlay
        BufferedImage productImg = loadImage(product.getMainImage());
        int imgBottom = H * 50 / 100;
        if (productImg != null) {
            float scale = Math.max(W / (float) productImg.getWidth(), imgBottom / (float) productImg.getHeight());
            int dw = (int) (productImg.getWidth() * scale);
            int dh = (int) (productImg.getHeight() * scale);
            g.drawImage(productImg, (W - dw) / 2, (imgBottom - dh) / 2, dw, dh, null);
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(0, 0, W, imgBottom);
        } else {
            g.setColor(new Color(0x775836));
            g.fillRect(0, 0, W, imgBottom);
        }

        // Brand header — small text on image area
        drawCentered(g, "嘉怡珠宝", 48, Font.BOLD, 24, Color.WHITE);

        // ── Below image area (cream background) ──
        int yCursor = imgBottom + 34;

        // Product name
        drawCentered(g, truncate(product.getName(), 14), yCursor, Font.BOLD, 34, new Color(0x1C1B1B));
        yCursor += 48;

        // Subtitle
        if (hasText(product.getSubtitle())) {
            drawCentered(g, truncate(product.getSubtitle(), 24), yCursor, Font.PLAIN, 20, new Color(0x605E5A));
            yCursor += 38;
        }

        // Bottom section - QR code & description (same row, left=desc, right=QR)
        int qrSize = 120;
        int qrX = W - qrSize - 36;
        int qrY = H - qrSize - 48;
        int qrCenterY = qrY + qrSize / 2;

        // Description text — left of QR code, same row, vertically centered
        String descRaw = product.getDescriptionText();
        if (!hasText(descRaw)) descRaw = stripHtml(product.getDescription());
        log.info("描述文本: {}", descRaw != null ? descRaw.substring(0, Math.min(60, descRaw.length())) : "null");
        if (hasText(descRaw)) {
            int dlX = 44, dlMaxW = 280;
            g.setFont(new Font("SansSerif", Font.PLAIN, 16));
            g.setColor(new Color(0x4F453C));
            FontMetrics dfm = g.getFontMetrics();
            int lh = dfm.getHeight();
            String src = descRaw.trim();

            int lineStart = 0, lineY = qrCenterY - lh;
            for (int ln = 1; ln <= 3; ln++) {
                if (lineStart >= src.length()) break;
                String chunk = src.substring(lineStart);
                int end = chunk.length();
                while (end > 0 && dfm.stringWidth(chunk.substring(0, end)) > dlMaxW) end--;
                if (end <= 0) break;
                boolean isLastWanted = (ln == 3 || lineStart + end >= src.length());
                String line;
                if (isLastWanted && lineStart + end < src.length()) {
                    while (end > 0 && dfm.stringWidth(chunk.substring(0, end) + "\u2026") > dlMaxW) end--;
                    line = (end > 0 ? chunk.substring(0, end) : "") + "\u2026";
                } else {
                    line = chunk.substring(0, end);
                }
                g.drawString(line, dlX, lineY);
                lineY += lh;
                lineStart += end;
            }
        }

        // "长按扫一扫" text
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.setColor(new Color(0x605E5A));
        FontMetrics fm = g.getFontMetrics();
        String scanTip = "长按扫码进店";
        g.drawString(scanTip, qrX + (qrSize - fm.stringWidth(scanTip)) / 2, qrY - 8);

        // Try WeChat Mini Program Code
        BufferedImage qrImage = null;
        try {
            byte[] qrBytes = wechatService.getWXACodeUnlimit("id=" + productId, "pages/product/detail");
            qrImage = ImageIO.read(new ByteArrayInputStream(qrBytes));
        } catch (Exception e) {
            log.warn("微信小程序码获取失败，使用 zxing 备用二维码: {}", e.getMessage());
            try {
                String content = "weixin://dl/business/?appid=wxff03445f8c883241"
                        + "&path=pages%2Fproduct%2Fdetail%3Fid%3D" + productId;
                BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, qrSize, qrSize);
                qrImage = MatrixToImageWriter.toBufferedImage(matrix);
            } catch (Exception e2) {
                log.warn("zxing 二维码生成失败: {}", e2.getMessage());
            }
        }

        if (qrImage != null) {
            // White background for QR
            g.setColor(Color.WHITE);
            g.fillRect(qrX - 6, qrY - 6, qrSize + 12, qrSize + 12);
            g.drawImage(qrImage, qrX, qrY, qrSize, qrSize, null);
        }

        // Brand watermark bottom-left
        g.setColor(new Color(0x775836));
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        fm = g.getFontMetrics();
        g.drawString("嘉怡珠宝", 36, H - 32);

        g.dispose();

        // Save to file
        try {
            File file = new File(posterDir, productId + ".png");
            ImageIO.write(poster, "png", file);
            String url = baseUrl + "/uploads/posters/" + productId + ".png";
            log.info("海报生成成功: {}", url);
            return url;
        } catch (Exception e) {
            throw new RuntimeException("海报保存失败: " + e.getMessage());
        }
    }

    private void drawCentered(Graphics2D g, String text, int y, int style, int size, Color color) {
        g.setFont(new Font("SansSerif", style, size));
        g.setColor(color);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (500 - fm.stringWidth(text)) / 2, y);
    }

    private BufferedImage loadImage(String url) {
        if (url == null || url.isEmpty()) return null;
        try {
            if (url.contains("/uploads/")) {
                String rel = url.substring(url.indexOf("/uploads/") + "/uploads/".length());
                File f = new File(posterDir.getParentFile(), rel);
                if (f.exists()) return ImageIO.read(f);
            }
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<byte[]> res = httpClient.send(req, HttpResponse.BodyHandlers.ofByteArray());
            return ImageIO.read(new ByteArrayInputStream(res.body()));
        } catch (Exception e) {
            log.warn("加载图片失败: {}", url);
            return null;
        }
    }

    private String stripHtml(String html) {
        if (html == null) return "";
        return html.replaceAll("<[^>]+>", "").replaceAll("\\s+", " ").trim();
    }

    private boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
