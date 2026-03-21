package org.cobalt.internal.chat

import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.ChatEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.inGroup
import org.cobalt.api.module.setting.impl.CheckboxSetting

/**
 * Ports SkyHanni's ChatFilter, WatchdogHider, and ArachneChatMessageHider into
 * a single Cobalt module.  Each toggle maps to one SkyHanni config entry so
 * anything that was filterable there works here too.
 */
object ChatFilterModule : Module("Chat Filter") {

    // ── Lobby ──────────────────────────────────────────────────────────────────
    private val filterLobbyJoins   = CheckboxSetting("Lobby Joins",    "Hide '… joined the lobby!' messages.",          false).inGroup("Lobby")
    private val filterMysteryBox   = CheckboxSetting("Mystery Boxes",  "Hide mystery box / mystery dust messages.",      false).inGroup("Lobby")
    private val filterPrototype    = CheckboxSetting("Prototype Lobby","Hide prototype lobby welcome & Hype messages.",  false).inGroup("Lobby")
    private val filterTournament   = CheckboxSetting("Tournaments",    "Hide Hypixel tournament announcement spam.",     false).inGroup("Lobby")

    // ── Warping ────────────────────────────────────────────────────────────────
    private val filterWarping      = CheckboxSetting("Warping",        "Hide 'Sending to server…' / 'Warping…' lines.", false).inGroup("Warping")
    private val filterWelcome      = CheckboxSetting("Welcome",        "Hide 'Welcome to Hypixel SkyBlock!'.",           false).inGroup("Warping")
    private val filterProfileJoin  = CheckboxSetting("Profile Join",   "Hide 'You are playing on profile…' lines.",     false).inGroup("Warping")

    // ── Combat ─────────────────────────────────────────────────────────────────
    private val filterKillCombo    = CheckboxSetting("Kill Combo",     "Hide kill combo progress messages.",             false).inGroup("Combat")
    private val filterSlayer       = CheckboxSetting("Slayer Quest",   "Hide slayer quest start / end messages.",        false).inGroup("Combat")
    private val filterSlayerDrops  = CheckboxSetting("Slayer Drops",   "Hide slayer rare drop messages.",                false).inGroup("Combat")
    private val filterUselessDrops = CheckboxSetting("Useless Drops",  "Hide worthless rare drop messages.",             false).inGroup("Combat")
    private val filterSoloClass    = CheckboxSetting("Solo Class",     "Hide 'stats doubled' solo class messages.",      false).inGroup("Combat")
    private val filterFairy        = CheckboxSetting("Fairy",          "Hide fairy death / revive messages.",            false).inGroup("Combat")
    private val filterArachne      = CheckboxSetting("Arachne",        "Hide Arachne calling / crystal / venom spam.",   false).inGroup("Combat")
    private val filterAnnoyingSpam = CheckboxSetting("Annoying Spam",  "Hide Implosion/spirit sceptre hit messages.",    false).inGroup("Combat")

    // ── Economy ────────────────────────────────────────────────────────────────
    private val filterBzAhMinis    = CheckboxSetting("BZ / AH Ops",   "Hide 'Processing bid…' / 'Claiming order…' etc.",false).inGroup("Economy")
    private val filterBzOrders     = CheckboxSetting("BZ Orders",     "Hide buy/sell order setup confirmation lines.",   false).inGroup("Economy")
    private val filterSacrifice    = CheckboxSetting("Sacrifice",     "Hide dragon sacrifice broadcast messages.",        false).inGroup("Economy")
    private val filterLegacyItems  = CheckboxSetting("Legacy Items",  "Hide legacy item exchange warnings.",             false).inGroup("Economy")

    // ── Garden ─────────────────────────────────────────────────────────────────
    private val filterGardenNoPest = CheckboxSetting("No Pest Msg",   "Hide 'There are no pests in your garden' spam.",  true).inGroup("Garden")
    private val filterJacobFortune = CheckboxSetting("Jacob Fortune", "Hide Jacob NPC fortune talisman messages.",       false).inGroup("Garden")
    private val filterParkour      = CheckboxSetting("Parkour",       "Hide parkour start/checkpoint/finish messages.",  false).inGroup("Garden")
    private val filterTeleportPad  = CheckboxSetting("Teleport Pads", "Hide teleport pad warp / error messages.",        false).inGroup("Garden")

    // ── Events ─────────────────────────────────────────────────────────────────
    private val filterGuildExp     = CheckboxSetting("Guild / Event EXP", "Hide GEXP and Event EXP earned messages.",    false).inGroup("Events")
    private val filterWinterGift   = CheckboxSetting("Winter Gift",   "Hide winter gift reward spam.",                   false).inGroup("Events")
    private val filterWinterIsland = CheckboxSetting("Winter Island", "Hide Snow Cannon mount messages.",                false).inGroup("Events")
    private val filterEventLevelUp = CheckboxSetting("Event Level Up","Hide event level-up broadcast messages.",         false).inGroup("Events")
    private val filterFireSale     = CheckboxSetting("Fire Sale",     "Hide fire sale announcement spam.",               false).inGroup("Events")
    private val filterRewardBundles= CheckboxSetting("Reward Bundles","Hide unclaimed seasonal reward reminder spam.",   false).inGroup("Events")
    private val filterFactoryUpgrade=CheckboxSetting("Factory Upgrade","Hide Chocolate Factory upgrade messages.",       false).inGroup("Events")
    private val filterHoppityEggs  = CheckboxSetting("Hoppity Eggs",  "Hide Hoppity's Hunt egg-appeared messages.",      false).inGroup("Events")
    private val filterSkyMall      = CheckboxSetting("Sky Mall",      "Hide Sky Mall buff-changed messages.",            false).inGroup("Events")
    private val filterLottery      = CheckboxSetting("Lottery",       "Hide Lottery buff-changed messages.",             false).inGroup("Events")

    // ── Misc ───────────────────────────────────────────────────────────────────
    private val filterWatchdog     = CheckboxSetting("Watchdog",      "Hide multi-line Watchdog ban announcement blocks.", false).inGroup("Misc")
    private val filterUselessWarn  = CheckboxSetting("Useless Warnings","Hide 'You can't use this in combat' etc.",      false).inGroup("Misc")
    private val filterUselessNotifs= CheckboxSetting("Useless Notifs","Hide mining speed, tipping, bank interest spam.", false).inGroup("Misc")
    private val filterPartyDivider = CheckboxSetting("Party Dividers","Hide §9§m--- party separator lines.",             false).inGroup("Misc")
    private val filterAhDivider    = CheckboxSetting("AH Dividers",   "Hide §b--- auction house separator lines.",       false).inGroup("Misc")
    private val filterEmpty        = CheckboxSetting("Empty Lines",   "Hide completely empty chat messages.",            false).inGroup("Misc")
    private val filterDungeonStats = CheckboxSetting("Dungeon Stats", "Hide solo-stats and rare-drop dungeon messages.",  false).inGroup("Misc")

    // ── Patterns ──────────────────────────────────────────────────────────────

    // Lobby joins
    private val lobbyJoinRx    = Regex("""(?: §b>§c>§a>§r §r)?.* §6(?:joined|(?:spooked|slid) into) the lobby!(?:§r §a<§c<§b<)?""")
    private val hypixelSmpRx   = Regex("""§2[\s]*?§aYou can now create your own Hypixel SMP server![\s]*?""")
    private val snowParticlesRx= Regex("""[\s]*?.*§bFor the best experience.*Snow.*Particles.*""", RegexOption.DOT_MATCHES_ALL)

    // Mystery box / dust
    private val mysteryBoxRx   = Regex("""§b✦ §r.* §r§7found (?:a|an) §r.*(?:Mystery Box|in a §r§a(?:Holiday )?Mystery Box)§r§7!""")
    private val mysteryDustRx  = Regex("""§b§b✦ §r§7You earned §r§b\d+ §r§7Mystery Dust!""")
    private val petConsumRx    = Regex("""§b§b✦ §r§7You earned §a\d+ §7Pet Consumables items!""")

    // Warping
    private val warpingRx      = Regex("""§7(?:Sending to server|Request join for (?:Hub|Dungeon Hub #)) .*\.\.\.""")
    private val warpedToRx     = Regex("""§dWarped to (.*)§r§d!""")

    // Guild / Event EXP
    private val guildExpRx     = Regex("""§aYou earned §r§[0-9a-f][\d,]+ (?:GEXP|Event EXP) (?:§r§a\+ §r§[0-9a-f][\d,]+ Event EXP )?§r§afrom playing SkyBlock!""")

    // Kill combo
    private val killComboRx    = Regex("""§.§l\+(.*) Kill Combo(.*)""")
    private val killComboExpRx = Regex("""§cYour Kill Combo has expired! You reached a (.*) Kill Combo!""")

    // Slayer quest
    private val slayerStartRx  = Regex(""" {2}§r§5§lSLAYER QUEST STARTED!""")
    private val slayerEndRx    = Regex(""" {2}§r§a§lSLAYER QUEST COMPLETE!""")
    private val slayerXpRx     = Regex(""" {3}§r§5§l» §r§7Talk to Maddox to claim your (.*) Slayer XP!""")
    private val slayerLvlRx    = Regex(""" {3}§r§e(.*)Slayer LVL 9 §r§5- §r§a§lLVL MAXED OUT!""")
    private val slayerQuestRx  = Regex(""" {3}§5§l» §7Slay §c(.*) Combat XP §7worth of (.*)§7\.""")

    // Slayer drops
    private val slayerDropRxList = listOf(
        Regex("""§b§lRARE DROP! §r§7\(§r§f§r§7(.*)x §r§f§r§9(?:Revenant Viscera|Foul Flesh|Toxic Arrow Poison|Twilight Arrow Poison)§r§7\) (.*)"""),
        Regex("""§b§lRARE DROP! §r§7\(§r§f§r§9(?:Revenant Viscera|Foul Flesh)§r§7\) (.*)"""),
        Regex("""§6§lRARE DROP! §r§5Golden Powder (.*)"""),
        Regex("""§[59]§lVERY RARE DROP! {2}§r§7\(.*\) (.*)"""),
        Regex("""§6§lRARE DROP! §r§9Arachne's Keeper Fragment (.+)"""),
        Regex("""§6§lRARE DROP! §r§5Travel Scroll to Spider's Den Top of Nest (.+)"""),
        Regex("""§9§lVERY RARE DROP! {2}§r§7\(§r§f§r§a◆ Bite Rune I§r§7\) (.+)"""),
        Regex("""§5§lVERY RARE DROP! {2}§r§7\(§r§9Bane of Arthropods VI§r§7\) (.+)"""),
        Regex("""§9§lVERY RARE DROP! {2}§r§7\(§r§f§r§7\d+x §r§f§r§9(?:Glowstone|Blaze Rod|Magma Cream|Nether Wart) Distillate§r§7\) (.*)"""),
        Regex("""§d§lCRAZY RARE DROP! {2}§r§7\(§r§f§r§fPocket Espresso Machine§r§7\) (.*)"""),
        Regex("""§5§lVERY RARE DROP! {2}§r§7\(§r§f§r§5(?:◆ End Rune I|Sinful Dice|Revenant Catalyst|Undead Catalyst|◆ Endersnake Rune I|Transmission Tuner)§r§7\) (.*)"""),
        Regex("""§9§lVERY RARE DROP! {2}§r§7\(§r§f§r§9(?:Null Atom|Mana Steal I)§r§7\) (.*)"""),
        Regex("""§6§lRARE DROP! §r§5Bundle of Magma Arrows§r§7\) (.*)"""),
        Regex("""§9§lVERY RARE DROP! {2}§r§7\(§r§f§r§fWisp's Ice-Flavored Water I Splash Potion§r§7\) (.*)"""),
        Regex("""§5§lVERY RARE DROP! {2}§r§7\(§r§f§r§6Hazmat Enderman§r§7\) .*"""),
    )

    // Useless drops
    private val uselessDropRxList = listOf(
        Regex("""§6§lRARE DROP! §r§aEnchanted Ender Pearl (.*)"""),
        Regex("""§6§lRARE DROP! §r§f(?:Carrot|Potato) (.*)"""),
        Regex("""§6§lRARE DROP! §r§9Machine Gun Bow (.*)"""),
        Regex("""§6§lRARE DROP! §r§5(?:Earth Shard|Zombie Lord Chestplate) (.*)"""),
    )

    // Economy / annoying spam
    private val annoyingSpamRxList = listOf(
        Regex("""§7Your (?:Implosion|Molten Wave|Spirit Sceptre) hit (.*) for §r§c(.*) §r§7damage\."""),
        Regex("""§cYou need a tool with a §r§aBreaking Power §r§cof §r§6(\d)§r§c to mine .*"""),
    )

    // Bazaar / AH confirmation patterns
    private val bzOrderRxList = listOf(
        Regex("""§eBuy Order Setup! §r§a(.*)§r§7x (.*) §r§7for §r§6(.*) coins§r§7\."""),
        Regex("""§eSell Offer Setup! §r§a(.*)§r§7x (.*) §r§7for §r§6(.*) coins§r§7\."""),
        Regex("""§cCancelled! §r§7Refunded §r§6(.*) coins §r§7from cancelling buy order!"""),
        Regex("""§cCancelled! §r§7Refunded §r§a(.*)§r§7x (.*) §r§7from cancelling sell offer!"""),
    )

    // Winter island
    private val winterIslandRx = Regex("""§r§f☃ §r§7§r(.*) §r§7mounted a §r§fSnow Cannon§r§7!""")

    // Fire sale
    private val fireSaleMainRx  = Regex("""§6§k§lA§r §c§lFIRE SALE §r§6§k§lA""", RegexOption.DOT_MATCHES_ALL)
    private val fireSaleRxList  = listOf(
        Regex("""§c♨ §eFire Sales? for .* §e(?:are|is) starting soon!"""),
        Regex("""§c\s*♨ .* (?:Skin|Rune|Dye) §e(?:for a limited time )?\(.* §eleft\)(?:§c|!)"""),
        Regex("""§c♨ §eVisit the Community Shop in the next §c.* §eto grab yours! §a§l\[WARP]"""),
        Regex("""§c♨ §eA Fire Sale for .* §eis starting soon!"""),
        Regex("""§c♨ §r§eFire Sales? for .* §r§eended!"""),
        Regex("""§c {3}♨ §eAnd \d+ more!"""),
    )

    // Events
    private val eventLvlRxList  = listOf(
        Regex("""(?:§f)? +§r§7You are now §r§.Event Level §r§.*§r§7!"""),
        Regex("""(?:§f)? +§r§7You earned §r§.* Event Silver§r§7!"""),
        Regex("""(?:§f)? +§r§.§k#§r§. LEVEL UP! §r§.§k#"""),
    )

    // Factory upgrade / Hoppity
    private val factoryUpgradeRxList = listOf(
        Regex(""".* §r§7has been promoted to §r§7\[.*§r§7] §r§.*§r§7!"""),
        Regex("""§7Your §r§aRabbit Barn §r§7capacity has been increased to §r§a.* Rabbits§r§7!"""),
        Regex("""§7You will now produce §r§6.* Chocolate §r§7per click!"""),
        Regex("""§7You upgraded to §r§d.*?§r§7!"""),
    )
    private val hoppityAppearRx = Regex("""§d§lHOPPITY'S HUNT §r§dA .* §r§dhas appeared!""")
    private val hoppityBeginRx  = Regex("""§dHoppity's Hunt §r§ehas begun!.*""")

    // Sacrifice
    private val sacrificeRxList = listOf(
        Regex("""§c§lSACRIFICE! (.*) §r§eturned (.*) §r§einto (.*) Dragon Essence§r§e!"""),
        Regex("""§c§lBONUS LOOT! §r§eThey also received (.*) §r§efrom their sacrifice!"""),
    )

    // Reward bundles (seasonal reminders)
    private val rewardBundleRxList = listOf(
        Regex("""(?:§.)*You haven't claimed your (?:§.)*\w+ Rewards (?:§.)*yet!"""),
        Regex("""(?:§.)*Talk to the (?:§.)*.+(?:§.)*in the (?:§.)*.+(?:§.)*!"""),
    )

    // Jacob fortune (NPC message outside garden)
    private val jacobFortuneRx  = Regex("""§e\[NPC] Jacob§f: §rYour §9Anita's \w+ §fis giving you §6\+\d{1,2}☘ .+ Fortune §fduring the contest!""")

    // SkyMall / Lottery
    private val skymallRxList   = listOf("§bNew day! §r§eYour §r§2Sky Mall §r§ebuff changed!", "§8§oYou can disable this messaging by toggling Sky Mall in your /hotm!")
    private val lotteryRxList   = listOf("§bNew day! §r§eYour §r§2Lottery §r§ebuff changed!", "§8§oYou can disable this messaging by toggling Lottery in your /hotf!")

    // Solo class / solo stats / dungeon fairy
    private val soloClassRx     = Regex("""§6Your §r§a(Healer|Mage|Berserk|Archer|Tank) §r§6stats are doubled because you are the only player using this class!""")
    private val soloStatsRx     = Regex("""§a\[(Healer|Mage|Berserk|Archer|Tank)].*""")
    private val fairyRxList     = listOf(
        Regex("""§d[\w']+ the Fairy§r§f: You killed me! Take this §r§6Revive Stone §r§fso that my death is not in vain!"""),
        Regex("""§d[\w']+ the Fairy§r§f: You killed me! I'll revive you so that my death is not in vain!"""),
        Regex("""§d[\w']+ the Fairy§r§f: Have a great life!"""),
    )
    private val dungeonRareDropRx = Regex("""§6§lRARE REWARD! §r§bLeebys §r§efound a (.*) §r§ein their (.*) Chest§r§e!""")

    // Garden pest
    private val noPestRxList    = listOf(
        Regex("""§aNo pests are currently in your garden!"""),
        Regex("""§eThere are no pests in your garden!"""),
        Regex("""§cThere are no pests currently in your garden""", RegexOption.IGNORE_CASE),
    )

    // Parkour
    private val parkourRxList   = listOf(
        Regex("""§aStarted parkour (.*)!"""),
        Regex("""§aFinished parkour (.*) in (.*)!"""),
        Regex("""§aReached checkpoint #(.*) for parkour (.*)!"""),
        Regex("""§4Wrong checkpoint for parkour (.*)!"""),
        Regex("""§4You haven't reached all checkpoints for parkour (.*)!"""),
    )

    // Teleport pad
    private val teleportPadRx   = Regex("""§aWarped from the (.*) §r§ato the (.*)§r§a!""")

    // Arachne
    private val arachneCallingRx  = Regex("""§4☄ §r.* §r§eplaced an §r§9Arachne's Calling§r§e!.*""")
    private val arachneCrystalRx  = Regex("""§4☄ §r.* §r§eplaced an Arachne Crystal! Something is awakening!""")
    private val arachneSpawnRx    = Regex("""§c\[BOSS] Arachne§r§f: (?:The Era of Spiders begins now\.|Ahhhh\.\.\.A Calling\.\.\.)""")
    private val arachneVenomRx    = Regex("""§dArachne(?:'s (?:Keeper|Brood))? used §r§2Venom Shot §r§don you hitting you for §r§c[\d.,]+ damage §r§dand infecting you with venom\.""")

    // ── Watchdog state ────────────────────────────────────────────────────────
    private var inWatchdog           = false
    private var watchdogBlockedLines = 0

    // ── Arachne state ─────────────────────────────────────────────────────────
    private var hideArachneDeadMsg = false

    // ── Exact-string fast sets ─────────────────────────────────────────────────

    private val lobbyExact = setOf(
        "  §r§f§l➤ §r§6You have reached your Hype limit! Add Hype to Prototype Lobby minigames by right-clicking with the Hype Diamond!",
    )
    private val warpingExact = setOf(
        "§7Warping...", "§7Warping you to your SkyBlock island...", "§7Warping using transfer token...",
        "§7Finding player...", "§7Sending a visit request...",
    )
    private val lobbyContains = setOf(
        "§r§6§lWelcome to the Prototype Lobby§r",
        "§r§e§6§lHYPIXEL§e is hosting a §b§lBED WARS DOUBLES§e tournament!",
        "§r§e§6§lHYPIXEL BED WARS DOUBLES§e tournament is live!",
        "§r§e§6§lHYPIXEL§e is hosting a §b§lTNT RUN§e tournament!",
        "§aYou are still radiating with §bGenerosity§r§a!",
    )
    private val warpingContains = setOf<String>() // handled in warpingExact

    private val bzAhMiniExact = setOf(
        "§7Putting item in escrow...", "§7Putting coins in escrow...",
        "§7Setting up the auction...", "§7Processing purchase...", "§7Processing bid...",
        "§7Claiming BIN auction...",
        "§6[Bazaar] §r§7Submitting sell offer...", "§6[Bazaar] §r§7Submitting buy order...",
        "§6[Bazaar] §r§7Executing instant sell...", "§6[Bazaar] §r§7Executing instant buy...",
        "§6[Bazaar] §r§7Cancelling order...", "§6[Bazaar] §r§7Claiming order...",
        "§6[Bazaar] §r§7Putting goods in escrow...",
        "§8Depositing coins...", "§8Withdrawing coins...",
    )
    private val slayerExact = setOf(
        "  §r§6§lNICE! SLAYER BOSS SLAIN!", "§eYou received kill credit for assisting on a slayer miniboss!",
    )
    private val uselessDropExact = setOf(
        "§6§lRARE DROP! §r§aEnchanted Ender Pearl",
        "§6§lRARE DROP! §r§aEnchanted End Stone",
        "§6§lRARE DROP! §r§5Crystal Fragment",
    )
    private val uselessNotifExact = setOf(
        "§eYour previous §r§6Plasmaflux Power Orb §r§ewas removed!",
        "§aYou used your §r§6Mining Speed Boost §r§aPickaxe Ability!",
        "§cYour Mining Speed Boost has expired!",
        "§a§r§6Mining Speed Boost §r§ais now available!",
        "§aYou have just received §r§60 coins §r§aas interest in your personal bank account!",
        "§aSince you've been away you earned §r§60 coins §r§aas interest in your personal bank account!",
        "§aYou have just received §r§60 coins §r§aas interest in your co-op bank account!",
    )
    private val uselessNotifPatterns = listOf(
        Regex("""(?:§a)?§aYou tipped \d+ players? in \d+(?: different)? games?!"""),
    )
    private val uselessWarningExact = setOf(
        "§cYou are sending commands too fast! Please slow down.",
        "§cYou can't use this while in combat!",
        "§cYou can not modify your equipped armor set!",
        "§cPlease wait a few seconds between refreshing!",
        "§cThis item is not salvageable!",
        "§cPlace a Dungeon weapon or armor piece above the anvil to salvage it!",
        "§cWhoa! Slow down there!",
        "§cWait a moment before confirming!",
        "§cYou cannot open the SkyBlock menu while in combat!",
    )
    private val annoyingSpamExact = setOf(
        "§cThere are blocks in the way!",
        "§aYour Blessing enchant got you double drops!",
        "§cYou can't use the wardrobe in combat!",
        "§6§lGOOD CATCH! §r§bYou found a §r§fFish Bait§r§b.",
        "§6§lGOOD CATCH! §r§bYou found a §r§aGrand Experience Bottle§r§b.",
        "§6§lGOOD CATCH! §r§bYou found a §r§aBlessed Bait§r§b.",
        "§6§lGOOD CATCH! §r§bYou found a §r§fDark Bait§r§b.",
        "§6§lGOOD CATCH! §r§bYou found a §r§fLight Bait§r§b.",
        "§6§lGOOD CATCH! §r§bYou found a §r§aHot Bait§r§b.",
        "§6§lGOOD CATCH! §r§bYou found a §r§fSpooky Bait§r§b.",
        "§e[NPC] Jacob§f: §rMy contest has started!",
        "§eObtain a §r§6Booster Cookie §r§efrom the community shop in the hub!",
    )
    private val fireSaleExact = setOf(
        "§6§k§lA§r §c§lFIRE SALE §r§6§k§lA",
        "§c♨ §eSelling multiple items for a limited time!",
    )
    private val ahDividerExact = setOf(
        "§b-----------------------------------------------------",
        "§eVisit the Auction House to collect your item!",
    )
    private val powderMiningExact = setOf(
        "§aYou uncovered a treasure chest!",
        "§aYou received §r§f1 §r§aWishing Compass§r§a.",
        "§aYou received §r§f1 §r§9Ascension Rope§r§a.",
        "§aYou received §r§f1 §r§aOil Barrel§r§a.",
        "§6You have successfully picked the lock on this chest!",
    )
    private val legacyItemsRx = Regex("""§cYou currently have one or more Legacy Items in your inventory or sacks.*""")
    private val teleportPadExact = setOf("§4This Teleport Pad does not have a destination set!")

    // ── Init ──────────────────────────────────────────────────────────────────

    init {
        addSetting(
            // Lobby
            filterLobbyJoins, filterMysteryBox, filterPrototype, filterTournament,
            // Warping
            filterWarping, filterWelcome, filterProfileJoin,
            // Combat
            filterKillCombo, filterSlayer, filterSlayerDrops, filterUselessDrops,
            filterSoloClass, filterFairy, filterArachne, filterAnnoyingSpam,
            // Economy
            filterBzAhMinis, filterBzOrders, filterSacrifice, filterLegacyItems,
            // Garden
            filterGardenNoPest, filterJacobFortune, filterParkour, filterTeleportPad,
            // Events
            filterGuildExp, filterWinterGift, filterWinterIsland, filterEventLevelUp,
            filterFireSale, filterRewardBundles, filterFactoryUpgrade, filterHoppityEggs,
            filterSkyMall, filterLottery,
            // Misc
            filterWatchdog, filterUselessWarn, filterUselessNotifs, filterPartyDivider,
            filterAhDivider, filterEmpty, filterDungeonStats,
        )
        EventBus.register(this)
    }

    // ── Event ─────────────────────────────────────────────────────────────────

    @SubscribeEvent
    fun onChat(event: ChatEvent.Receive) {
        val msg = event.message ?: return
        if (shouldCancel(msg)) event.setCancelled(true)
    }

    private fun shouldCancel(msg: String): Boolean {

        // ── Empty ──────────────────────────────────────────────────────────────
        if (filterEmpty.value && msg.isBlank()) return true

        // ── Watchdog (multi-line block hider) ──────────────────────────────────
        if (filterWatchdog.value) {
            when (msg) {
                "§f" -> { /* potential start — let through, track below */ }
                "§4[WATCHDOG ANNOUNCEMENT]" -> { inWatchdog = true; watchdogBlockedLines = 0; return true }
                "§c" -> if (inWatchdog) { inWatchdog = false; return true }
            }
            if (inWatchdog) {
                watchdogBlockedLines++
                if (watchdogBlockedLines > 10) { inWatchdog = false; watchdogBlockedLines = 0 }
                return true
            }
        }

        // ── Arachne (stateful separator blocks) ───────────────────────────────
        if (filterArachne.value) {
            if (msg == "§a§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬") {
                hideArachneDeadMsg = !hideArachneDeadMsg; return true
            }
            if (msg == "                              §r§6§lARACHNE DOWN!") { hideArachneDeadMsg = true }
            if (hideArachneDeadMsg) return true
            if (arachneVenomRx.containsMatchIn(msg)) return true
            if (arachneCallingRx.containsMatchIn(msg)) return true
            if (arachneCrystalRx.containsMatchIn(msg)) return true
            if (arachneSpawnRx.containsMatchIn(msg)) return true
        }

        // ── Lobby ──────────────────────────────────────────────────────────────
        if (filterLobbyJoins.value) {
            if (lobbyJoinRx.containsMatchIn(msg)) return true
            if (hypixelSmpRx.containsMatchIn(msg)) return true
        }
        if (filterMysteryBox.value) {
            if (mysteryBoxRx.containsMatchIn(msg)) return true
            if (mysteryDustRx.containsMatchIn(msg)) return true
            if (petConsumRx.containsMatchIn(msg)) return true
        }
        if (filterPrototype.value && (msg in lobbyExact || lobbyContains.any { msg.contains(it) })) return true
        if (filterTournament.value && lobbyContains.filter { it.contains("HYPIXEL") }.any { msg.contains(it) }) return true

        // ── Warping ────────────────────────────────────────────────────────────
        if (filterWarping.value) {
            if (msg in warpingExact) return true
            if (warpingRx.containsMatchIn(msg)) return true
            if (warpedToRx.containsMatchIn(msg)) return true
        }
        if (filterWelcome.value && msg == "§eWelcome to §r§aHypixel SkyBlock§r§e!") return true
        if (filterProfileJoin.value && (msg.startsWith("§aYou are playing on profile: §e") || msg.startsWith("§8Profile ID: "))) return true

        // ── Guild / Event EXP ──────────────────────────────────────────────────
        if (filterGuildExp.value && guildExpRx.containsMatchIn(msg)) return true

        // ── Kill combo ─────────────────────────────────────────────────────────
        if (filterKillCombo.value) {
            if (msg == "§6§l+50 Kill Combo") return true
            if (killComboRx.containsMatchIn(msg)) return true
            if (killComboExpRx.containsMatchIn(msg)) return true
        }

        // ── Party dividers ─────────────────────────────────────────────────────
        if (filterPartyDivider.value && msg == "§9§m-----------------------------------------------------") return true

        // ── AH dividers ────────────────────────────────────────────────────────
        if (filterAhDivider.value && msg in ahDividerExact) return true

        // ── Slayer ─────────────────────────────────────────────────────────────
        if (filterSlayer.value) {
            if (msg in slayerExact) return true
            if (msg.startsWith("§e✆ RING... ")) return true
            if (slayerStartRx.containsMatchIn(msg)) return true
            if (slayerEndRx.containsMatchIn(msg)) return true
            if (slayerXpRx.containsMatchIn(msg)) return true
            if (slayerLvlRx.containsMatchIn(msg)) return true
            if (slayerQuestRx.containsMatchIn(msg)) return true
        }

        // ── Slayer drops ───────────────────────────────────────────────────────
        if (filterSlayerDrops.value && slayerDropRxList.any { it.containsMatchIn(msg) }) return true

        // ── Useless drops ──────────────────────────────────────────────────────
        if (filterUselessDrops.value) {
            if (msg in uselessDropExact) return true
            if (uselessDropRxList.any { it.containsMatchIn(msg) }) return true
        }

        // ── Dungeon stats & rare drops ─────────────────────────────────────────
        if (filterDungeonStats.value) {
            if (soloClassRx.containsMatchIn(msg)) return true
            if (soloStatsRx.containsMatchIn(msg)) return true
            if (fairyRxList.any { it.containsMatchIn(msg) }) return true
            if (dungeonRareDropRx.containsMatchIn(msg)) return true
        }

        // ── Sacrifice ──────────────────────────────────────────────────────────
        if (filterSacrifice.value && sacrificeRxList.any { it.containsMatchIn(msg) }) return true

        // ── Legacy items ───────────────────────────────────────────────────────
        if (filterLegacyItems.value && legacyItemsRx.containsMatchIn(msg)) return true

        // ── Useless notifications ──────────────────────────────────────────────
        if (filterUselessNotifs.value) {
            if (msg in uselessNotifExact) return true
            if (uselessNotifPatterns.any { it.containsMatchIn(msg) }) return true
        }

        // ── Useless warnings ───────────────────────────────────────────────────
        if (filterUselessWarn.value && msg in uselessWarningExact) return true

        // ── Annoying spam ──────────────────────────────────────────────────────
        if (filterAnnoyingSpam.value) {
            if (msg in annoyingSpamExact) return true
            if (annoyingSpamRxList.any { it.containsMatchIn(msg) }) return true
        }

        // ── Bazaar / AH mini operations ────────────────────────────────────────
        if (filterBzAhMinis.value && msg in bzAhMiniExact) return true

        // ── Bazaar order confirmations ─────────────────────────────────────────
        if (filterBzOrders.value && bzOrderRxList.any { it.containsMatchIn(msg) }) return true

        // ── Winter island ──────────────────────────────────────────────────────
        if (filterWinterIsland.value && winterIslandRx.containsMatchIn(msg)) return true

        // ── Winter gift ────────────────────────────────────────────────────────
        // Matches generic gift reward patterns (coins, XP, north stars, potions, books, enchants)
        if (filterWinterGift.value && isWinterGift(msg)) return true

        // ── SkyMall ────────────────────────────────────────────────────────────
        if (filterSkyMall.value && msg in skymallRxList) return true

        // ── Lottery ────────────────────────────────────────────────────────────
        if (filterLottery.value && msg in lotteryRxList) return true

        // ── Event level-up ─────────────────────────────────────────────────────
        if (filterEventLevelUp.value) {
            if (msg == "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬") return true
            if (eventLvlRxList.any { it.containsMatchIn(msg) }) return true
        }

        // ── Fire sale ──────────────────────────────────────────────────────────
        if (filterFireSale.value) {
            if (msg in fireSaleExact) return true
            if (fireSaleMainRx.containsMatchIn(msg)) return true
            if (fireSaleRxList.any { it.containsMatchIn(msg) }) return true
        }

        // ── Reward bundles ─────────────────────────────────────────────────────
        if (filterRewardBundles.value && rewardBundleRxList.any { it.containsMatchIn(msg) }) return true

        // ── Chocolate factory upgrades ─────────────────────────────────────────
        if (filterFactoryUpgrade.value && factoryUpgradeRxList.any { it.containsMatchIn(msg) }) return true

        // ── Hoppity's Hunt ─────────────────────────────────────────────────────
        if (filterHoppityEggs.value && (hoppityAppearRx.containsMatchIn(msg) || hoppityBeginRx.containsMatchIn(msg))) return true

        // ── Jacob fortune ──────────────────────────────────────────────────────
        if (filterJacobFortune.value && jacobFortuneRx.containsMatchIn(msg)) return true

        // ── Garden: No Pests ───────────────────────────────────────────────────
        if (filterGardenNoPest.value && noPestRxList.any { it.containsMatchIn(msg) }) return true

        // ── Parkour ────────────────────────────────────────────────────────────
        if (filterParkour.value) {
            if (parkourRxList.any { it.containsMatchIn(msg) }) return true
            if (msg in setOf("§4Cancelled parkour! You cannot fly.", "§4Cancelled parkour! You cannot use item abilities.", "§4Cancelled parkour!")) return true
        }

        // ── Teleport pads ──────────────────────────────────────────────────────
        if (filterTeleportPad.value) {
            if (teleportPadRx.containsMatchIn(msg)) return true
            if (msg in teleportPadExact) return true
        }

        // ── Solo class ─────────────────────────────────────────────────────────
        if (filterSoloClass.value && soloClassRx.containsMatchIn(msg)) return true

        return false
    }

    // Winter gift messages share a generic reward structure; match a few key prefixes
    private val winterGiftRxList = listOf(
        Regex("""§aYou received §r§6\d[\d,.]* Coins §r§afrom a (?:Great|Good|Normal|Perfect|Magical)? ?Winter Gift!"""),
        Regex("""§aYou received §r§b\d[\d,.]* §r§6SkyBlock XP §r§afrom a .*Winter Gift!"""),
        Regex("""§aYou received §r§b\d[\d,.]* §r§bNorth Stars §r§afrom a .*Winter Gift!"""),
        Regex("""§aYou received .* §r§afrom a .*Winter Gift!"""),
        Regex("""§7Right-click the gift to open it!"""),
        Regex("""§a\+\d+ (?:Farming|Mining|Combat|Foraging|Fishing|Enchanting|Alchemy) XP"""),
    )
    private fun isWinterGift(msg: String) = winterGiftRxList.any { it.containsMatchIn(msg) }
}
