export const PANEL_URL = import.meta.env.VITE_PANEL_URL ?? '/panel'

export const links = {
  signIn: `${PANEL_URL}/login`,
  getAccess: `${PANEL_URL}/register`,
  discord: 'https://discord.gg/phantom', // TODO: real Discord invite
}

export const nav: { label: string; href: string }[] = [
  { label: 'Features', href: '#features' },
  { label: 'Pricing', href: '#pricing' },
  { label: 'FAQ', href: '#faq' },
]

export interface Feature {
  /** SVG path `d` for a 24x24 stroke icon (viewBox 0 0 24 24). */
  iconPath: string
  title: string
  blurb: string
}

export const features: Feature[] = [
  {
    iconPath: 'M3 21l4-1 11-11a2.5 2.5 0 00-3.5-3.5L3.5 16.5 3 21z',
    title: 'Mining',
    blurb: 'Hands-free Dwarven Mines, Crystal Hollows, and gemstone routes with smart block selection.',
  },
  {
    iconPath: 'M14 4l6 6-9 9H5v-6l9-9zM3 21l3-3',
    title: 'Combat',
    blurb: 'Reactive targeting and clean rotations for grinding zealots, mobs, and bosses.',
  },
  {
    iconPath: 'M12 2l3 6 6 .9-4.5 4.4 1 6.2L12 17l-5.5 2.5 1-6.2L3 8.9 9 8z',
    title: 'Kuudra',
    blurb: 'Full Kuudra automation across tiers — supply runs, freshness, and the kill loop.',
  },
  {
    iconPath: 'M4 6h16M4 12h16M4 18h10',
    title: 'Commissions',
    blurb: 'Auto-complete Dwarven commissions end to end, claim rewards, and repeat.',
  },
  {
    iconPath: 'M12 2a10 10 0 100 20 10 10 0 000-20zM2 12h20M12 2c3 3 3 17 0 20M12 2c-3 3-3 17 0 20',
    title: 'Native pathfinder',
    blurb: 'A real C/C++ pathfinder via JNI — fast, smooth, human-like movement, not teleport hacks.',
  },
  {
    iconPath: 'M12 2l8 4v6c0 5-3.5 8-8 10-4.5-2-8-5-8-10V6l8-4zM9 12l2 2 4-4',
    title: 'Secure loader',
    blurb: 'Ed25519-signed manifests and in-memory encrypted modules. Auth fails closed by design.',
  },
]

export interface Tier {
  name: string
  price: string
  period: string
  highlight?: boolean
  perks: string[]
  cta: string
}

export const tiers: Tier[] = [
  {
    name: 'Monthly',
    price: '$X', // TODO: real price
    period: '/ month',
    perks: ['All modules', 'Native pathfinder', 'Community support', '1 device'],
    cta: 'Get Monthly',
  },
  {
    name: 'Quarterly',
    price: '$X', // TODO: real price
    period: '/ 3 months',
    highlight: true,
    perks: ['Everything in Monthly', 'Save vs. monthly', 'Priority support', '1 device'],
    cta: 'Get Quarterly',
  },
  {
    name: 'Lifetime',
    price: '$X', // TODO: real price
    period: 'one-time',
    perks: ['Everything in Quarterly', 'All future updates', 'Priority support', '1 device'],
    cta: 'Get Lifetime',
  },
]

export interface Faq {
  q: string
  a: string
}

export const faqs: Faq[] = [
  {
    q: 'Which Minecraft version does Phantom support?',
    a: 'Phantom targets the latest Hypixel Skyblock-compatible build via Fabric. The required version is shown in the panel before you download.',
  },
  {
    q: 'How do I get access after purchasing?',
    a: 'Create an account, redeem your key in the member panel, bind your device, then download the client — all from the panel.',
  },
  {
    q: 'Is it safe?',
    a: 'Modules are delivered as signed, in-memory encrypted bundles and the loader fails closed if authentication does not succeed. Use at your own discretion; account actions are always your responsibility.',
  },
  {
    q: 'Can I use it on multiple computers?',
    a: 'Each license binds to one device. Device rebinding is managed from the panel subject to the cooldown shown there.',
  },
  {
    q: 'Do you offer refunds?',
    a: 'Refund eligibility is described at checkout and in the panel. Reach out on Discord for help with any purchase issue.',
  },
]
