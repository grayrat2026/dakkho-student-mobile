/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './app/**/*.{js,jsx,ts,tsx}',
    './components/**/*.{js,jsx,ts,tsx}',
    './lib/**/*.{js,jsx,ts,tsx}',
  ],
  presets: ['nativewind/preset'],
  theme: {
    extend: {
      colors: {
        // DAKKHO Brand Colors — exact match from web app globals.css
        background: 'var(--background)',
        foreground: 'var(--foreground)',
        card: 'var(--card)',
        'card-foreground': 'var(--card-foreground)',
        primary: 'var(--primary)',
        'primary-foreground': 'var(--primary-foreground)',
        secondary: 'var(--secondary)',
        'secondary-foreground': 'var(--secondary-foreground)',
        muted: 'var(--muted)',
        'muted-foreground': 'var(--muted-foreground)',
        accent: 'var(--accent)',
        'accent-foreground': 'var(--accent-foreground)',
        destructive: 'var(--destructive)',
        border: 'var(--border)',
        input: 'var(--input)',
        ring: 'var(--ring)',
        sky: '#0ea5e9',
        'sky-deep': '#2563eb',
        'emerald-accent': '#10b981',
        'amber-accent': '#f59e0b',
        danger: '#ef4444',
        // Dark mode navy
        'dakkho-navy': '#0c1222',
        'dakkho-dark': '#0f172a',
      },
      fontFamily: {
        sans: ['Nunito-Regular'],
        bold: ['Nunito-Bold'],
        extrabold: ['Nunito-ExtraBold'],
        semibold: ['Nunito-SemiBold'],
      },
      borderRadius: {
        xl: '0.75rem',
        '2xl': '1rem',
        '3xl': '1.5rem',
      },
    },
  },
  plugins: [],
};
