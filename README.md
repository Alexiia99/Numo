# 🌿 Numo — Personal Expense Tracker

> *Know where your money goes. No cloud, no account, no drama.*

A clean, offline-first Android app for tracking personal expenses. Built because most finance apps are either too complex, require an account, or send your data somewhere you didn't ask for.

Numo stores everything locally using SQLite. Your data stays on your phone. Period.

---

## 🌸 Screenshots

| Home | Expenses | Charts | Budget Limits | Recurring | Settings |
|------|----------|--------|---------------|-----------|----------|
| ![Home](screenshots/home.png) | ![Expenses](screenshots/expenses.png) | ![Charts](screenshots/charts.png) | ![Limits](screenshots/limits.png) | ![Recurring](screenshots/recurring.png) | ![Settings](screenshots/settings.png) |

---

## 🌼 Features

### 🏠 Home
Monthly spending at a glance — total spent vs remaining budget. Recent transactions listed below. One tap to add a new expense.

### 📋 Expenses
Full transaction history grouped by date. Search by name, filter by category. Categories include: Alimentación, Hogar, Restaurante, Transporte, Salud, Ocio, Ropa, Otros.

### 🔁 Recurring expenses
Set up subscriptions and recurring payments once. Configure:
- **Periodicity** — monthly, quarterly, or yearly
- **Billing day** — which day of the month it hits
- **Advance notification** — 1 day, 3 days, or same day

### 🎯 Budget limits
Set spending caps per category with a visual progress bar. Edit or remove limits at any time.

### 📊 Charts & Stats
- Pie chart of spending by category with percentages
- Monthly evolution bar chart (last 6 months)
- Month-over-month comparison with % change

### ⚙️ Settings
- Custom currency
- Configurable month start day
- Global monthly budget
- Daily reminder notification
- Dark mode toggle
- **Export to CSV** — download all your expenses
- **Export to PDF** — monthly report
- Custom avatar

---

## 🛠️ Tech Stack

```
Language        →  Kotlin
UI              →  XML layouts · Material Design
Database        →  SQLite (local, no internet required)
Architecture    →  MVVM
Notifications   →  Android WorkManager
Export          →  CSV · PDF generation
Build           →  Gradle (Kotlin DSL)
```

---

## 🌻 Why offline-first?

Most expense apps sync to the cloud. That means an account, a privacy policy, and someone else's server holding your financial data.

Numo doesn't do any of that. SQLite, local storage, done. If you uninstall the app, your data is gone — which is actually a feature if you think about it.

The CSV and PDF export exist precisely so you can take your data wherever you want.

---

## 🚀 Getting Started

```bash
# Clone the repo
git clone https://github.com/Alexiia99/Numo

# Open in Android Studio
# File → Open → select the Numo folder

# Run on emulator or physical device (Android 8.0+)
```

No API keys. No configuration. No account needed. Just open and run.

---

## 🌸 Project Structure

```
app/
├── data/
│   ├── database/          # SQLite setup & DAOs
│   └── repository/        # Data access layer
├── ui/
│   ├── home/              # Home screen
│   ├── expenses/          # Expense list & filters
│   ├── recurring/         # Recurring payments
│   ├── limits/            # Budget caps
│   ├── charts/            # Stats & visualizations
│   └── settings/          # App configuration
├── model/                 # Data classes
└── utils/                 # CSV/PDF export, notifications
```

---

## 💚 What I learned building this

Designing a local-first app means thinking differently about data. No API to fall back on — if the SQLite schema is wrong, everything breaks. I spent more time on the database design than I expected.

The recurring expenses feature was the trickiest part: figuring out when to trigger notifications, how to handle missed payments, and how to make the periodicity logic work cleanly without overcomplicating the data model.

Also: export to PDF from an Android app is way more annoying than it sounds.

---

*Built with 🌿 — [github.com/Alexiia99](https://github.com/Alexiia99)*
