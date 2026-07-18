---
version: alpha
name: Jiayi Jewelry Mall（嘉怡珠宝）
description: >
  佳艺珠宝商城微信小程序设计规范。面向25-45岁女性用户的轻奢珠宝电商平台。
  整体风格精致、优雅、简约，突出珠宝的质感与光泽。
colors:
  primary: "#C8A27A"
  primary-light: "#E8D5C0"
  primary-dark: "#A67B52"
  secondary: "#F5F0EB"
  tertiary: "#D4AF37"
  neutral: "#FFFFFF"
  neutral-variant: "#F8F6F4"
  surface: "#FFFFFF"
  surface-variant: "#F5F0EB"
  background: "#FAFAF8"
  error: "#CF6679"
  on-primary: "#FFFFFF"
  on-secondary: "#3D3D3D"
  on-surface: "#1A1A1A"
  on-surface-variant: "#666666"
  outline: "#D9D2CC"
typography:
  h1:
    fontFamily: "Noto Serif SC, Songti SC, serif"
    fontSize: 48px
    fontWeight: 700
    lineHeight: 1.2
  h2:
    fontFamily: "Noto Serif SC, Songti SC, serif"
    fontSize: 36px
    fontWeight: 600
    lineHeight: 1.3
  h3:
    fontFamily: "Noto Serif SC, Songti SC, serif"
    fontSize: 28px
    fontWeight: 600
    lineHeight: 1.4
  h4:
    fontFamily: "PingFang SC, Helvetica Neue, sans-serif"
    fontSize: 22px
    fontWeight: 600
    lineHeight: 1.4
  body-large:
    fontFamily: "PingFang SC, Helvetica Neue, sans-serif"
    fontSize: 17px
    fontWeight: 400
    lineHeight: 1.6
  body:
    fontFamily: "PingFang SC, Helvetica Neue, sans-serif"
    fontSize: 15px
    fontWeight: 400
    lineHeight: 1.6
  body-small:
    fontFamily: "PingFang SC, Helvetica Neue, sans-serif"
    fontSize: 14px
    fontWeight: 400
    lineHeight: 1.5
  caption:
    fontFamily: "PingFang SC, Helvetica Neue, sans-serif"
    fontSize: 12px
    fontWeight: 400
    lineHeight: 1.4
  label:
    fontFamily: "PingFang SC, Helvetica Neue, sans-serif"
    fontSize: 12px
    fontWeight: 500
    lineHeight: 1.4
    letterSpacing: "0.05em"
rounded:
  xs: 4px
  sm: 8px
  md: 12px
  lg: 16px
  xl: 24px
  full: 9999px
spacing:
  xs: 4px
  sm: 8px
  md: 12px
  lg: 16px
  xl: 24px
  xxl: 32px
  xxxl: 48px
components:
  button-primary:
    backgroundColor: "{colors.primary}"
    textColor: "{colors.on-primary}"
    rounded: "{rounded.sm}"
    padding: "12px 24px"
    fontSize: "{typography.body.fontSize}"
    fontWeight: 600
  button-primary-hover:
    backgroundColor: "{colors.primary-dark}"
  button-secondary:
    backgroundColor: "{colors.secondary}"
    textColor: "{colors.on-secondary}"
    rounded: "{rounded.sm}"
    padding: "12px 24px"
  card:
    backgroundColor: "{colors.surface}"
    rounded: "{rounded.md}"
    elevation: 2
    padding: "{spacing.md}"
  input:
    backgroundColor: "{colors.surface}"
    rounded: "{rounded.sm}"
    borderColor: "{colors.outline}"
    padding: "{spacing.md}"
    fontSize: "{typography.body.fontSize}"
  tab-bar:
    backgroundColor: "{colors.surface}"
    activeColor: "{colors.primary}"
    inactiveColor: "{colors.on-surface-variant}"
    height: 56px
  badge:
    backgroundColor: "{colors.error}"
    textColor: "{colors.on-primary}"
    rounded: "{rounded.full}"
    fontSize: "{typography.caption.fontSize}"
---

## Overview

A luxury jewelry e-commerce WeChat mini-program targeting fashion-conscious women aged 25-45. The design communicates elegance, sophistication, and trust through the use of a rose gold / champagne color palette, generous white space, refined serif typography for headings, and subtle shadows that evoke the feel of a high-end boutique. Every screen should feel airy, uncluttered, and premium — like walking into a luxury jewelry store.

**Target audience**: Women, 25-45, seeking fine jewelry and fashion accessories
**Price positioning**: Mid-to-high end
**Key emotions**: Elegance, desire, trust, exclusivity

## Visual Theme & Atmosphere

Design with a "luxury boutique in your pocket" feeling. Use abundant white/light backgrounds to let product images shine. Photography is the hero — jewel tones and sparkle should come from the product imagery, not from UI chrome. Transitions should be smooth and deliberate (300ms ease-in-out). Cards should have a subtle shadow (box-shadow: 0 2px 8px rgba(0,0,0,0.06)) to create depth without heaviness.

**Atmosphere keywords**: Refined, warm, luminous, tactile, feminine, modern-classic

## Layout & Grid

- **Design base width**: 375px (WeChat mini-program standard)
- **Content max-width**: 375px, centered
- **Grid**: 4-column grid for icon grid, 2-column waterfall for product listings
- **Horizontal padding**: 16px on each side
- **Card spacing**: 12px between cards
- **Bottom safe area**: Reserve 56px for tab bar + bottom safe area

## Elevation

- **Cards**: elevation 2 (subtle shadow)
- **Bottom tab bar**: elevation 4 (slightly raised)
- **Modal/Dialog**: elevation 12
- **Floating action area**: elevation 6
- **Product image area**: elevation 1 (near-flat, image speaks for itself)

## Motion & Animation

- **Page transitions**: Slide right-to-left for forward navigation, reverse for back
- **Fade in**: Content sections fade in with 300ms delay
- **Micro-interactions**: Button press scales to 0.97, heart icon bounces on tap
- **Loading skeleton**: Shimmer effect for product cards loading
- **AI generation**: Pulsing gradient animation while waiting for result

---

## Page-by-Page Design Specification

### Page 1: Home Page (首页)

**Vibe**: Warm, inviting, luxurious — like walking into a flagship boutique.

**Layout structure** (top to bottom):

1. **Top bar** (44px)
   - Left: Logo text "佳艺珠宝" in primary color, serif font
   - Right: Search icon button

2. **Banner Carousel** (height: 180px)
   - Full-width image carousel with rounded bottom corners (16px)
   - Page indicator dots at bottom center, using primary color
   - Auto-play every 3 seconds, swipeable
   - Each banner links to a different destination (product/activity/page)

3. **Icon Navigation Grid** (2 rows × 3 columns, ~160px)
   - Each icon: Circle icon (48×48px) above label text
   - Spacing: 12px between icons
   - 6 entries: 限时活动, 限时拍卖, 新品上市, AI穿戴, 积分商城, 热门推荐
   - Icons: Use line-art style with primary color, 24px stroke width
   - Labels: caption font, centered under icon

4. **Section: "限时抢购" (Flash Sale)** (~240px)
   - Section header row:
     - Left: "限时抢购" heading (h4, primary-dark color)
     - Right: Countdown timer "⏰ 02:15:30" + "更多 >" link
   - Horizontal scrollable card list (scroll-snap, no scrollbar)
   - Each card (140×200px):
     - Product image (120×120px, rounded md)
     - Product name (2 lines max, body-small)
     - Current price (primary color, bold) + original price (strikethrough, caption)
   - Background tint: light surface-variant

5. **Section: "热门推荐" (Hot Picks)** 
   - Section header: "热门推荐" heading (h4)
   - 2-column waterfall grid
   - Each product card (~170px wide):
     - Image (aspect ratio 3:4, rounded lg)
     - Product name (body, 1 line, overflow ellipsis)
     - Price (body, primary color, bold)
     - Optional: tag badge (新品/热销) in top-left corner
   - Pull-to-refresh, infinite scroll loading

**States**: Loading → shimmer skeleton, Empty → "暂无推荐" with illustration

---

### Page 2: Category Page (分类)

**Vibe**: Organized, browsable, efficient — like a well-arranged jewelry display case.

**Layout**:

1. **Search bar** (44px) - rounded full, placeholder "搜索珠宝..."
   - Optional: scan icon on right for visual search

2. **Split pane**:
   - **Left sidebar** (80px wide):
     - Full-height scrollable list
     - Each category item: vertical label, active state has primary color bar indicator
     - Categories: 全部, 戒指, 项链, 耳饰, 手链, 手镯, 套装, 其他
   - **Right content area** (remaining width):
     - Category header: category name + result count
     - **Sort bar**: 综合 | 销量 | 价格↑ | 新品 (horizontal pill filters)
     - **Product grid**: 2-column grid, same card style as home page
     - **Filter button**: floating in bottom-right, opens filter drawer
       - Filter options: price range slider, material, style tags

**States**: Empty category → "该分类暂无商品" with illustration

---

### Page 3: Discover Page (发现 🌟)

**Vibe**: Social, inspirational, community-driven — like flipping through a luxury lifestyle magazine.

**Layout**:

1. **Page title**: "发现" in h3 style

2. **Tab bar** (3 tabs, segmented control style):
   - AI穿搭秀 | 珠宝指南 | 达人晒单
   - Active tab: primary color underline indicator

3. **Tab: AI穿搭秀**
   - 2-column waterfall grid of AI-generated outfit photos
   - Each card:
     - Image (generated by AI, 3:4 ratio)
     - User avatar + nickname overlay at bottom
     - Bottom row: ❤️ 128 | 💬 23 (like and comment counts)
   - Tapping opens detail view with full image + comments

4. **Tab: 珠宝指南**
   - Article card list (single column)
   - Each card: horizontal layout
     - Left: thumbnail image (80×80px, rounded sm)
     - Right: title (body, bold), summary (body-small, 2 lines), date (caption)
   - Tapping opens article reader page

5. **Tab: 达人晒单**
   - Similar layout to AI穿搭秀 but with real product photos
   - Show product tag (linked to product detail page)
   - "同款购买" button overlay

---

### Page 4: Product Detail Page (商品详情)

**Vibe**: Immersive, detailed, persuasive — the virtual equivalent of holding jewelry in your hands.

**Layout**:

1. **Top navigation** (44px, transparent overlay on image)
   - Left: Back arrow
   - Right: Share icon, Heart icon (favorite toggle)

2. **Image carousel** (375×375px, full width square)
   - High-res product images, swipeable
   - Pinch-to-zoom
   - Dot indicators
   - Optional: first image can be a video (auto-play muted)

3. **Product info section**:
   - Product name (h3, serif)
   - Price row:
     - Current price (h2, primary color) 
     - Original price (body, strikethrough, on-surface-variant)
     - Discount badge (if applicable)
   - Sales info: "已售 256 件" | "好评率 99%"

4. **Specifications selector**:
   - "选择规格" row with horizontal pill options
   - Color/swatch options, size options

5. **Video link section** (38px height):
   - Icon ▶️ + "查看商品视频详情" text
   - Right arrow indicator
   - Tapping opens Douyin (抖音) app or external video link
   - Configurable per product in admin panel

6. **AI Wear button** (44px height):
   - Icon ✨ + "AI穿戴试试" text
   - Right: "仅会员" badge (if user not logged in or not member)
   - Tapping navigates to AI Wear page (if member, check daily limit)

7. **Product details** (collapsible section):
   - Rich text / HTML content from admin
   - Product parameters table:
     - 材质: 18K金
     - 重量: 约3.5g
     - 尺寸: 可调节
     - 产地: 中国

8. **Bottom action bar** (fixed, 56px + safe area):
   - Left: 💬 客服 | ❤️ 收藏
   - Right: "加入购物车" (secondary button) | "立即购买" (primary button)

**States**: Unavailable → gray overlay "已售罄"

---

### Page 5: AI Wear Page (AI穿戴)

**Vibe**: Magical, personal, exciting — like having a virtual fitting room.

**Layout**:

1. **Top bar**: "AI穿戴" title + close button

2. **Instruction area**: 
   - "上传您的照片，AI为您展示佩戴效果"
   - Sample images showing before/after

3. **Photo upload area** (center of page):
   - Large square area (280×280px) with dashed border
   - Default: camera icon + "点击拍照或上传照片"
   - After selection: image preview with "重新拍摄" button overlay
   - Two options: "📷 拍照" | "🖼️ 从相册选择"

4. **Generate button**: 
   - "✨ 开始生成" primary button (full width)
   - Below: "今日剩余: 3次" text (caption, on-surface-variant)
   - Disabled state when limit reached

5. **Result area** (shown after generation):
   - Side-by-side comparison: original photo vs AI result
   - "长按保存" / "分享给好友" / "发布到发现" action buttons
   - "看看其他款式" link to related products

6. **Loading state**: 
   - Full-screen overlay with gradient pulse animation
   - "AI正在为您生成..." text
   - Estimated wait: "约5-15秒"

**States**: Not member → member upgrade card with CTA button. Daily limit exhausted → "今日次数已用完" with illustration and "明天再来" message.

---

### Page 6: Cart Page (购物车)

**Vibe**: Clear, actionable, reassuring.

**Layout**:

1. **Page title**: "购物车" + edit button (toggle edit mode)

2. If empty:
   - Empty cart illustration
   - "购物车是空的" text
   - "去逛逛" CTA button

3. If has items:
   - **Store header** (optional, for multi-store)
   - **Cart item cards**:
     - Checkbox (left)
     - Product image (80×80px, rounded sm)
     - Product info:
       - Name (body, bold)
       - Specification (body-small, on-surface-variant)
       - Price (body, primary color)
     - Quantity stepper (minus / number / plus)
     - Delete button (visible in edit mode)
   - **Recommendation section**: "你可能还喜欢" horizontal scroll

4. **Bottom bar** (fixed):
   - Left: Select all checkbox + "全选"
   - Middle: "合计: ¥2,580.00" (primary color, bold)
   - Right: "结算 (3)" primary button

---

### Page 7: Checkout Page (结算)

**Vibe**: Trustworthy, smooth, efficient.

**Layout**:

1. **Address section**:
   - If no address: "请添加收货地址" with add button
   - If has address: card showing name, phone, address details
   - Right arrow to select/edit address

2. **Order items summary**: 
   - Compact list of items (image + name + spec + qty + price)

3. **Discount section**:
   - 优惠券: "选择优惠券 >" or "无可用优惠券"
   - 积分抵扣: toggle + "可用 3580 积分，可抵扣 ¥35.80"

4. **Order summary**:
   - 商品小计: ¥2,580.00
   - 运费: ¥0.00 (包邮)
   - 优惠: -¥50.00
   - **实付金额**: ¥2,530.00 (h3, primary color, bold)

5. **Order note**: optional text input

6. **Submit button**: "提交订单" primary button (full width)

**Payment**: Opens WeChat Pay standard payment sheet

---

### Page 8: Member Page (会员)

**Vibe**: Exclusive, rewarding, aspirational.

**Layout**:

1. **Member card** (hero section):
   - Background: primary gradient
   - Avatar + "用户名" + "💎 钻石会员" 
   - Points: "当前积分 3,580" 
   - Progress bar: "距离黄金会员还需 420 积分"
   - Card-style with rounded xl, subtle shadow

2. **Daily check-in**:
   - "📅 签到" button (if not checked in today)
   - "✅ 已签到 +5积分" (if done)
   - Streak display: "连续签到 7天"

3. **Member benefits list** (icon list):
   - ✨ AI穿戴每日 10 次
   - 🎂 生日礼遇
   - 🚚 免邮特权
   - 💬 专属客服

4. **AI Wear remaining**: 
   - "今日AI穿戴剩余: 3/10 次" with progress indicator

5. **积分商城入口**: 
   - "前往积分商城兑换好礼 >"

---

### Page 9: My Profile Page (我的)

**Vibe**: Personal, organized, functional.

**Layout**:

1. **Profile header**:
   - Avatar (64×64px, rounded full)
   - Nickname + "💎 钻石会员" badge
   - Right arrow to edit profile

2. **My Orders section**:
   - Section header: "我的订单" + "查看全部 >"
   - 5 icon buttons in a row:
     - 📄 待付款 | 📦 待发货 | 🚚 待收货 | ⭐ 已完成 | 🔧 售后

3. **Quick links** (icon + text list):
   - ❤️ 我的收藏 (with count)
   - ✨ 我的AI穿搭 (with count)
   - 🎁 积分明细
   - 📍 收货地址
   - 💬 联系客服
   - ⚙️ 设置

---

### Page 10: Auction Page (限时拍卖)

**Vibe**: Exciting, competitive, urgent.

**Layout**:

1. **Page title**: "限时拍卖" + auction rules icon

2. **Auction item cards**:
   - Product image (full width, 3:4 aspect)
   - Overlay at bottom:
     - Product name (h4, white text with shadow)
     - "当前出价 ¥2,800" (h3, tertiary color)
     - "起拍价 ¥1,000" (body-small)
   - Countdown timer (large, prominent): "⏰ 02:15:30"
   - Bid count: "23人出价"
   - "出价" primary button

3. **Bid history**: 
   - Expandable section showing recent bids (avatar, name, amount, time)

4. **My bids** section (if user has bid):
   - Current status: "最高出价者" or "已被超过，去加价"

---

### Page 11: Activity Page (活动)

**Vibe**: Festive, promotional, engaging.

**Layout**:

1. **Page title**: "活动中心"

2. **Active promotions**:
   - Card for each active activity:
     - Activity image/banner (full width, rounded lg)
     - Activity title (h4)
     - Activity period: "2026.07.10 - 2026.07.20"
     - Rule summary (body-small)
     - "立即参与" button

3. **Coupon center**:
   - Available coupons to claim
   - Each coupon card: discount amount, condition, expiry date, "领取" button

---

## Do's and Don'ts

- **Do** use primary color (#C8A27A) only for key interactive elements and price displays — overuse dilutes its premium feel
- **Do** leave generous white space around product images — jewelry needs room to breathe
- **Do** use serif fonts (Noto Serif SC) for product names and section headings to convey elegance
- **Don't** use more than 2 font sizes on a single screen — maintain clear hierarchy
- **Don't** use bright, saturated colors — they compete with the jewelry photography
- **Do** maintain WCAG AA contrast ratios (4.5:1 for normal text, 3:1 for large text)
- **Don't** crowd the bottom tab bar with more than 5 items
- **Do** show skeleton loading states before content arrives
- **Don't** use autoplaying video with sound — it's jarring in a luxury context
- **Do** use subtle micro-animations (button press, heart bounce) to delight users
- **Don't** use heavy shadows or strong borders — keep it light and airy
- **Do** make AI Wear results feel magical with a polished loading animation

## Accessibility

- All touch targets minimum 44×44px
- Color contrast follows WCAG AA standards
- Images have alt text descriptions
- Semantic heading hierarchy (h1→h4)
- Focus indicators for keyboard navigation in admin panel
