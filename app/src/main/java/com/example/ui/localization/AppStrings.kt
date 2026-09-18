package com.example.ui.localization

interface AppStrings {
    // Navigation
    val navHome: String
    val navChallenges: String
    val navArena: String
    val navLeaderboard: String
    val navRewards: String
    val navProfile: String
    val navVip: String
    val navSettings: String
    val navAbout: String

    // Common
    val appName: String
    val playNow: String
    val cancel: String
    val confirm: String
    val close: String
    val claim: String
    val claimed: String
    val locked: String
    val unlock: String
    val level: String
    val xp: String
    val coins: String
    val tickets: String
    val streakDays: String
    val rank: String
    val victory: String
    val defeat: String
    val retry: String
    val back: String

    // Home
    val greetingHello: String
    val featuredChallenge: String
    val dailyChallenge: String
    val continueChallenge: String
    val leaderboardPreview: String
    val dailyMissions: String
    val viewAll: String
    val arenaStatusOnline: String
    val totalCompetitors: String

    // Stage & Level System
    val currentStage: String
    val nextStage: String
    val stageUnlockRequirement: String
    val stageProgress: String
    val stageComplete: String
    val bestRecord: String

    // Pre-Game Instruction
    val challengeInstructionTitle: String
    val howToPlay: String
    val objective: String
    val winCondition: String
    val winConditionDesc: String
    val antiCheatNotice: String
    val startCountdownBtn: String
    val reflexInstruction: String
    val mathInstruction: String

    // Result Screen
    val matchResultTitle: String
    val accuracy: String
    val maxComboLabel: String
    val newRecord: String
    val nextChallengeBtn: String
    val playAgainBtn: String
    val returnHomeBtn: String
    val xpProgressTitle: String

    // Pause & Exit Dialogs
    val pauseTitle: String
    val pause: String
    val resume: String
    val quitMatch: String
    val confirmExitMatch: String
    val stayInMatch: String
    val exitMatch: String
    val antiCheatFlagged: String

    // Challenges
    val challengesTitle: String
    val tabAll: String
    val tabQuick: String
    val tabDaily: String
    val tabTimeAttack: String
    val tabAccuracy: String
    val tabEndless: String
    val tabTournaments: String
    val tabSpecial: String
    val participants: String
    val reward: String
    val durationSec: String
    val startMatch: String

    // Arena Game
    val arenaTitle: String
    val arenaSubtitle: String
    val roundScore: String
    val comboMultiplier: String
    val timeLeft: String
    val tapToHit: String
    val solveProblem: String
    val gameOver: String
    val finalScore: String
    val xpEarned: String
    val coinsEarned: String
    val returnToLobby: String
    val selectChallengeToPlay: String
    val countdownReady: String
    val countdownGo: String

    // Leaderboard
    val leaderboardTitle: String
    val globalRanking: String
    val weeklyTournament: String
    val dailySprint: String
    val friendsCircle: String
    val yourRank: String
    val podiumFirst: String
    val podiumSecond: String
    val podiumThird: String
    val winsLabel: String
    val scoreLabel: String
    val demoDataNotice: String

    // Rewards
    val rewardsTitle: String
    val dailyStreakTitle: String
    val dailyStreakDesc: String
    val watchAdForRewards: String
    val tapsellAdButton: String
    val adWatching: String
    val adRewardClaimed: String
    val mysteryChests: String
    val purchaseReward: String

    // VIP
    val vipTitle: String
    val vipBadge: String
    val vipBannerTitle: String
    val vipBannerDesc: String
    val vipPerk1: String
    val vipPerk2: String
    val vipPerk3: String
    val vipPerk4: String
    val vipPerk5: String
    val vipMonthly: String
    val vipYearly: String
    val vipBestValue: String
    val subscribeBazaar: String
    val restorePurchases: String
    val vipActiveNotice: String
    val vipExpiresOn: String
    val purchasingWait: String
    val purchaseFailedNotice: String

    // Profile
    val profileTitle: String
    val winRate: String
    val totalMatches: String
    val bestScoreLabel: String
    val achievementsTitle: String
    val matchHistoryTitle: String
    val noHistory: String
    val noAchievements: String
    val viewStats: String

    // Settings
    val settingsTitle: String
    val sectionLanguage: String
    val sectionTheme: String
    val sectionAudio: String
    val sectionGeneral: String
    val soundEffects: String
    val hapticFeedback: String
    val notifications: String
    val resetProgressTitle: String
    val resetProgressDesc: String
    val privacyPolicyTitle: String
    val privacyPolicyContent: String

    // About
    val aboutTitle: String
    val developerNameLabel: String
    val developerName: String
    val appVersionLabel: String
    val appVersion: String
    val copyrightLabel: String
    val bazaarReadyNotice: String
    val tapsellReadyNotice: String

    // Errors & Notices
    val emptyChallenges: String
    val errorGeneral: String
    val notEnoughCoins: String
    val notEnoughTickets: String
    val matchSuccessNotice: String
}

object PersianStrings : AppStrings {
    override val navHome = "خانه"
    override val navChallenges = "چالش‌ها"
    override val navArena = "میدان نبرد"
    override val navLeaderboard = "رتبه‌بندی"
    override val navRewards = "جوایز"
    override val navProfile = "پروفایل"
    override val navVip = "اشتراک VIP"
    override val navSettings = "تنظیمات"
    override val navAbout = "درباره بازی"

    override val appName = "میدان نبرد چالش"
    override val playNow = "شروع نبرد"
    override val cancel = "انصراف"
    override val confirm = "تأیید"
    override val close = "بستن"
    override val claim = "دریافت جایزه"
    override val claimed = "دریافت شد"
    override val locked = "قفل است"
    override val unlock = "بازگشایی"
    override val level = "سطح"
    override val xp = "امتیاز XP"
    override val coins = "سکه"
    override val tickets = "بلیت"
    override val streakDays = "روز متوالی"
    override val rank = "رتبه"
    override val victory = "پیروزی درخشان!"
    override val defeat = "شکست در چالش!"
    override val retry = "تلاش مجدد"
    override val back = "بازگشت"

    override val greetingHello = "به میدان نبرد خوش آمدید!"
    override val featuredChallenge = "چالش‌های منتخب روز"
    override val dailyChallenge = "ماموریت ویژه روزانه"
    override val continueChallenge = "ادامه رقابت"
    override val leaderboardPreview = "پیش‌نمایش برترین‌ها"
    override val dailyMissions = "ماموریت‌های روزانه"
    override val viewAll = "مشاهده همه"
    override val arenaStatusOnline = "وضعیت آفلاین / ذخیره‌سازی محلی"
    override val totalCompetitors = "شرکت‌کنندگان فعال"

    // Stage & Level
    override val currentStage = "مرحله فعلی"
    override val nextStage = "مرحله بعدی"
    override val stageUnlockRequirement = "شرایط بازگشایی"
    override val stageProgress = "پیشرفت مرحله"
    override val stageComplete = "مرحله تسخیر شد!"
    override val bestRecord = "بهترین رکورد"

    // Pre-game instruction
    override val challengeInstructionTitle = "راهنما و ماموریت چالش"
    override val howToPlay = "نحوه بازی"
    override val objective = "هدف چالش"
    override val winCondition = "شرط پیروزی"
    override val winConditionDesc = "کسب حداقل ۲۰۰ امتیاز در زمان تعیین شده برای دریافت پاداش کامل"
    override val antiCheatNotice = "سیستم آنتی‌چیت فعال است و سرعت واکنش و الگوی لمس را ارزیابی می‌کند."
    override val startCountdownBtn = "آماده‌ام! شروع نبرد"
    override val reflexInstruction = "با سرعت و دقت بالا روی هدف‌های نئونی ضربه بزنید. هدف‌های طلایی ۳ برابر امتیاز دارند! ضربات متوالی ضریب کامبو را تا ۱۰ برابر افزایش می‌دهد."
    override val mathInstruction = "معادلات محاسباتی ذهنی سریع را با انتخاب گزینه صحیح پاسخ دهید. پاسخ اشتباه کامبو را صفر می‌کند."

    // Result screen
    override val matchResultTitle = "گزارش عملکرد نبرد"
    override val accuracy = "دقت ضربات"
    override val maxComboLabel = "حداکثر کامبو"
    override val newRecord = "🌟 رکورد جدید ثبت شد!"
    override val nextChallengeBtn = "چالش بعدی ◀"
    override val playAgainBtn = "تلاش دوباره"
    override val returnHomeBtn = "بازگشت به خانه"
    override val xpProgressTitle = "پیشرفت سطح و پاداش‌ها"

    // Pause & Exit
    override val pauseTitle = "بازی متوقف شد"
    override val pause = "توقف"
    override val resume = "ادامه بازی"
    override val quitMatch = "خروج از بازی"
    override val confirmExitMatch = "آیا مطمئن هستید که می‌خواهید از نبرد خارج شوید؟ امتیاز این دور ذخیره نخواهد شد."
    override val stayInMatch = "ادامه رقابت"
    override val exitMatch = "خروج قطعی"
    override val antiCheatFlagged = "امتیاز غیرعادی توسط سامانه ضدتقلب مسدود شد."

    // Challenges
    override val challengesTitle = "تالار چالش‌های آرنا"
    override val tabAll = "همه"
    override val tabQuick = "سرعتی"
    override val tabDaily = "روزانه"
    override val tabTimeAttack = "حمله به زمان"
    override val tabAccuracy = "دقت و تمرکز"
    override val tabEndless = "بی‌پایان"
    override val tabTournaments = "تورنمنت‌ها"
    override val tabSpecial = "رویدادها"
    override val participants = "شرکت‌کننده"
    override val reward = "جایزه"
    override val durationSec = "ثانیه"
    override val startMatch = "ورود به نبرد"

    // Arena Game
    override val arenaTitle = "میدان فعال مسابقه"
    override val arenaSubtitle = "تمرکز کنید، سریع بزنید و رکورد بزنید!"
    override val roundScore = "امتیاز دور"
    override val comboMultiplier = "ضریب کامبو"
    override val timeLeft = "زمان باقی‌مانده"
    override val tapToHit = "روی هدف ضربه بزنید!"
    override val solveProblem = "معادله را حل کنید:"
    override val gameOver = "پایان وقت مسابقه!"
    override val finalScore = "امتیاز نهایی"
    override val xpEarned = "امتیاز XP کسب شده"
    override val coinsEarned = "سکه کسب شده"
    override val returnToLobby = "بازگشت به لابی"
    override val selectChallengeToPlay = "یک چالش را از فهرست انتخاب کنید"
    override val countdownReady = "آماده نبرد باشید..."
    override val countdownGo = "شروع!"

    // Leaderboard
    override val leaderboardTitle = "لیدربورد قهرمانان"
    override val globalRanking = "جهانی"
    override val weeklyTournament = "هفتگی"
    override val dailySprint = "روزانه"
    override val friendsCircle = "دوستان"
    override val yourRank = "رتبه شما"
    override val podiumFirst = "مقام اول زرین"
    override val podiumSecond = "مقام دوم سیمین"
    override val podiumThird = "مقام سوم برنز"
    override val winsLabel = "برد"
    override val scoreLabel = "امتیاز"
    override val demoDataNotice = "وضعیت آفلاین: دیتابیس محلی فعال است"

    // Rewards
    override val rewardsTitle = "مرکز دریافت جوایز"
    override val dailyStreakTitle = "حضور متوالی روزانه"
    override val dailyStreakDesc = "هر روز وارد برنامه شوید و جوایز تصاعدی سکه و بلیت دریافت کنید!"
    override val watchAdForRewards = "تماشای ویدیوی تبلیغاتی تپسل"
    override val tapsellAdButton = "مشاهده ویدیو (+۱۵۰ سکه و +۵۰ XP)"
    override val adWatching = "در حال بارگذاری و پخش ویدیوی تپسل..."
    override val adRewardClaimed = "تبلیغ تأیید شد! پاداش با موفقیت به حساب افزوده شد."
    override val mysteryChests = "صندوق‌های اسرارآمیز"
    override val purchaseReward = "خرید با سکه"

    // VIP
    override val vipTitle = "اشتراک طلایی VIP"
    override val vipBadge = "نشان ویژه VIP"
    override val vipBannerTitle = "قدرت بی‌پایان در آرنا"
    override val vipBannerDesc = "بدون تبلیغات، دو برابر XP، بلیت نامحدود تورنمنت‌ها و هاله اختصاصی"
    override val vipPerk1 = "حذف کامل تبلیغات در سراسر برنامه"
    override val vipPerk2 = "دو برابر شدن امتیاز XP در تمام مسابقات"
    override val vipPerk3 = "ورود رایگان به تورنمنت‌ها بدون کسر بلیت"
    override val vipPerk4 = "هاله درخشان طلایی در لیدربورد کشوری"
    override val vipPerk5 = "صندوق پاداش افسانه‌ای ماهانه"
    override val vipMonthly = "اشتراک ماهانه VIP"
    override val vipYearly = "اشتراک سالانه VIP (۳۵٪ تخفیف)"
    override val vipBestValue = "بهترین انتخاب"
    override val subscribeBazaar = "خرید اشتراک از کافه‌بازار"
    override val restorePurchases = "بازیابی خریدهای قبلی"
    override val vipActiveNotice = "اشتراک VIP شما هم‌اکنون فعال است!"
    override val vipExpiresOn = "تاریخ انقضای اشتراک:"
    override val purchasingWait = "در حال اتصال به درگاه پرداخت کافه‌بازار..."
    override val purchaseFailedNotice = "پرداخت ناموفق بود یا لغو گردید."

    // Profile
    override val profileTitle = "پروفایل کاربری"
    override val winRate = "نرخ پیروزی"
    override val totalMatches = "تعداد بازی‌ها"
    override val bestScoreLabel = "بالاترین رکورد"
    override val achievementsTitle = "نشان‌ها و دستاوردها"
    override val matchHistoryTitle = "تاریخچه بازی‌های اخیر"
    override val noHistory = "هنوز بازی ثبت نشده است. وارد یک نبرد شوید!"
    override val noAchievements = "هنوز دستاوردی بازگشایی نشده است."
    override val viewStats = "آمار کامل"

    // Settings
    override val settingsTitle = "تنظیمات بازی"
    override val sectionLanguage = "زبان برنامه / Language"
    override val sectionTheme = "پوسته دیداری"
    override val sectionAudio = "صدا و لرزش"
    override val sectionGeneral = "عمومی و داده‌ها"
    override val soundEffects = "جلوه‌های صوتی"
    override val hapticFeedback = "بازخورد لرزشی (Haptic)"
    override val notifications = "اعلان‌ها"
    override val resetProgressTitle = "بازنشانی داده‌های محلی"
    override val resetProgressDesc = "تمام سوابق، سکه‌ها و امتیازات محلی را پاک می‌کند."
    override val privacyPolicyTitle = "حریم خصوصی"
    override val privacyPolicyContent = "این برنامه به حریم خصوصی کاربران احترام می‌گذارد و اطلاعات هویتی جمع‌آوری نمی‌کند."

    // About
    override val aboutTitle = "درباره Arena Clash"
    override val developerNameLabel = "سازنده و توسعه‌دهنده:"
    override val developerName = "سیدحمید موسوی زاده"
    override val appVersionLabel = "نسخه برنامه:"
    override val appVersion = "1.0.0"
    override val copyrightLabel = "حق نشر:"
    override val bazaarReadyNotice = "متصل به سامانه پرداخت رسمی کافه‌بازار"
    override val tapsellReadyNotice = "یکپارچه با شبکه تبلیغاتی هوشمند تپسل پلاس"

    // Empty & Errors
    override val emptyChallenges = "چالشی در این دسته‌بندی یافت نشد."
    override val errorGeneral = "خطایی رخ داد. لطفاً دوباره تلاش کنید."
    override val notEnoughCoins = "سکه کافی برای این آیتم ندارید!"
    override val notEnoughTickets = "بلیت کافی برای ورود به این تورنمنت ندارید!"
    override val matchSuccessNotice = "نبرد به پایان رسید! امتیاز و پاداش‌ها با موفقیت ذخیره شدند."
}

object EnglishStrings : AppStrings {
    override val navHome = "Home"
    override val navChallenges = "Challenges"
    override val navArena = "Arena"
    override val navLeaderboard = "Ranks"
    override val navRewards = "Rewards"
    override val navProfile = "Profile"
    override val navVip = "VIP Pass"
    override val navSettings = "Settings"
    override val navAbout = "About"

    override val appName = "Arena Clash"
    override val playNow = "Play Now"
    override val cancel = "Cancel"
    override val confirm = "Confirm"
    override val close = "Close"
    override val claim = "Claim"
    override val claimed = "Claimed"
    override val locked = "Locked"
    override val unlock = "Unlock"
    override val level = "Level"
    override val xp = "XP Points"
    override val coins = "Coins"
    override val tickets = "Tickets"
    override val streakDays = "Day Streak"
    override val rank = "Rank"
    override val victory = "Glorious Victory!"
    override val defeat = "Challenge Defeat!"
    override val retry = "Try Again"
    override val back = "Back"

    override val greetingHello = "Welcome Arena Champion!"
    override val featuredChallenge = "Featured Arena Battle"
    override val dailyChallenge = "Daily Special Quest"
    override val continueChallenge = "Continue Battles"
    override val leaderboardPreview = "Top Leaderboard Preview"
    override val dailyMissions = "Daily Missions"
    override val viewAll = "View All"
    override val arenaStatusOnline = "Offline / Local Storage"
    override val totalCompetitors = "Active Competitors"

    override val currentStage = "Current Stage"
    override val nextStage = "Next Stage"
    override val stageUnlockRequirement = "Unlock Requirement"
    override val stageProgress = "Stage Progress"
    override val stageComplete = "Stage Conquered!"
    override val bestRecord = "Best Record"

    override val challengeInstructionTitle = "Challenge Briefing & Guide"
    override val howToPlay = "How to Play"
    override val objective = "Objective"
    override val winCondition = "Victory Condition"
    override val winConditionDesc = "Score at least 200 points within the time limit to win and claim full rewards"
    override val antiCheatNotice = "Anti-Cheat is active, evaluating genuine human reaction speed and precision."
    override val startCountdownBtn = "I'm Ready! Enter Battle"
    override val reflexInstruction = "Tap glowing neon targets as fast as possible. Golden targets award 3X points! Consecutive hits increase combo multiplier up to 10X!"
    override val mathInstruction = "Solve rapid mental arithmetic equations by tapping the correct answer. Wrong choices reset your combo multiplier."

    override val matchResultTitle = "Match Performance Summary"
    override val accuracy = "Accuracy"
    override val maxComboLabel = "Max Combo"
    override val newRecord = "🌟 NEW HIGH SCORE RECORD!"
    override val nextChallengeBtn = "Next Challenge ◀"
    override val playAgainBtn = "Play Again"
    override val returnHomeBtn = "Return to Lobby"
    override val xpProgressTitle = "Level & XP Progression"

    override val pauseTitle = "Game Paused"
    override val pause = "Pause"
    override val resume = "Resume"
    override val quitMatch = "Quit Match"
    override val confirmExitMatch = "Are you sure you want to quit the match? Current round progress will not be saved."
    override val stayInMatch = "Continue Match"
    override val exitMatch = "Confirm & Exit"
    override val antiCheatFlagged = "Abnormal score blocked by Anti-Cheat verification."

    override val challengesTitle = "Challenge Arena Hall"
    override val tabAll = "All"
    override val tabQuick = "Quick"
    override val tabDaily = "Daily"
    override val tabTimeAttack = "Time Rush"
    override val tabAccuracy = "Accuracy"
    override val tabEndless = "Endless"
    override val tabTournaments = "Tournaments"
    override val tabSpecial = "Special"
    override val participants = "Players"
    override val reward = "Prize"
    override val durationSec = "sec"
    override val startMatch = "Enter Battle"

    override val arenaTitle = "Active Battle Arena"
    override val arenaSubtitle = "Focus, tap fast and shatter records!"
    override val roundScore = "Round Score"
    override val comboMultiplier = "Combo Boost"
    override val timeLeft = "Time Remaining"
    override val tapToHit = "Tap the target!"
    override val solveProblem = "Solve the equation:"
    override val gameOver = "Round Finished!"
    override val finalScore = "Final Score"
    override val xpEarned = "XP Earned"
    override val coinsEarned = "Coins Earned"
    override val returnToLobby = "Return to Lobby"
    override val selectChallengeToPlay = "Select a challenge from the list to start"
    override val countdownReady = "Get Ready..."
    override val countdownGo = "GO!"

    override val leaderboardTitle = "Champions Leaderboard"
    override val globalRanking = "Global"
    override val weeklyTournament = "Weekly"
    override val dailySprint = "Daily"
    override val friendsCircle = "Friends"
    override val yourRank = "Your Rank"
    override val podiumFirst = "Rank #1 Gold"
    override val podiumSecond = "Rank #2 Silver"
    override val podiumThird = "Rank #3 Bronze"
    override val winsLabel = "Wins"
    override val scoreLabel = "Score"
    override val demoDataNotice = "Offline Status: Local database active"

    override val rewardsTitle = "Reward Center"
    override val dailyStreakTitle = "Daily Login Streak"
    override val dailyStreakDesc = "Log in every day consecutively to claim progressively bigger prizes!"
    override val watchAdForRewards = "Watch Tapsell Video Ad"
    override val tapsellAdButton = "Watch Ad for +150 Coins & +50 XP"
    override val adWatching = "Loading and playing Tapsell Ad..."
    override val adRewardClaimed = "Ad verified! Bonus credited successfully."
    override val mysteryChests = "Mystery Chests"
    override val purchaseReward = "Buy with Coins"

    override val vipTitle = "VIP Subscription"
    override val vipBadge = "VIP Elite Emblem"
    override val vipBannerTitle = "Unleash Pure Arena Power"
    override val vipBannerDesc = "Zero ads, 2X XP bonus, unlimited tournaments, and exclusive badges!"
    override val vipPerk1 = "Ad-free uninterrupted gaming experience"
    override val vipPerk2 = "Double XP boost across all arena matches"
    override val vipPerk3 = "Unlimited tournament entry with no ticket fees"
    override val vipPerk4 = "Exclusive golden aura on leaderboards"
    override val vipPerk5 = "Monthly mythic reward chest"
    override val vipMonthly = "Monthly VIP Pass"
    override val vipYearly = "Annual VIP Pass (Save 35%)"
    override val vipBestValue = "BEST VALUE"
    override val subscribeBazaar = "Subscribe via Cafe Bazaar"
    override val restorePurchases = "Restore Purchases"
    override val vipActiveNotice = "Your VIP Subscription is Active!"
    override val vipExpiresOn = "Subscription Expiration Date:"
    override val purchasingWait = "Connecting to Cafe Bazaar Billing..."
    override val purchaseFailedNotice = "Payment was unsuccessful or canceled."

    override val profileTitle = "Player Profile"
    override val winRate = "Win Rate"
    override val totalMatches = "Matches"
    override val bestScoreLabel = "Best Record"
    override val achievementsTitle = "Trophies & Badges"
    override val matchHistoryTitle = "Recent Match History"
    override val noHistory = "No matches played yet. Dive into an arena challenge!"
    override val noAchievements = "No achievements unlocked yet."
    override val viewStats = "Full Stats"

    override val settingsTitle = "Settings"
    override val sectionLanguage = "Language"
    override val sectionTheme = "Visual Theme"
    override val sectionAudio = "Audio & Haptics"
    override val sectionGeneral = "General & Account"
    override val soundEffects = "Sound Effects"
    override val hapticFeedback = "Haptic Vibration"
    override val notifications = "Push Notifications"
    override val resetProgressTitle = "Reset Local Data"
    override val resetProgressDesc = "Resets user level, coins, and local achievements back to start."
    override val privacyPolicyTitle = "Privacy Policy"
    override val privacyPolicyContent = "This application protects user privacy. No personal identifiable information is gathered. Local caching utilizes Android Jetpack Room with offline capability."

    // About
    override val aboutTitle = "About Arena Clash"
    override val developerNameLabel = "Developer & Creator:"
    override val developerName = "Seyed Hamid Mousavizadeh"
    override val appVersionLabel = "Version:"
    override val appVersion = "1.0.0"
    override val copyrightLabel = "Copyright:"
    override val bazaarReadyNotice = "Engineered for Cafe Bazaar In-App Billing"
    override val tapsellReadyNotice = "Integrated with Tapsell Plus Monetization Network"

    // Errors & Notices
    override val emptyChallenges = "No challenges found in this category."
    override val errorGeneral = "An error occurred. Please try again."
    override val notEnoughCoins = "Not enough coins for this item!"
    override val notEnoughTickets = "Not enough tickets to enter this tournament!"
    override val matchSuccessNotice = "Battle completed! Score and rewards successfully saved."
}
