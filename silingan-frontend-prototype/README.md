# Silingan Community Platform - Frontend Prototype

Modern SaaS-style RBAC (Role-Based Access Control) community management platform prototype.

## 📋 Overview

This is a **frontend-only** interactive prototype based on the wireframe specifications in:
`../docs/COMMUNITY_RBAC_UI_WIREFRAME.md`

### Features Implemented

✅ **App Shell** - Community switcher, admin menu, tab navigation  
✅ **Staff Directory** - Search, filters, role management  
✅ **Edit Staff Access Modal** - Role selection, permissions display  
✅ **Admin Role & Access** - Permission matrix editor  
✅ **Dashboard** - Stats cards, charts, widgets  
✅ **Operations** - Maintenance, work orders, visitor passes  
✅ **Security** - Incident tracking  
✅ **Billing** - Bills, payments, collections  
✅ **Facilities** - Assets, parking, utility meters  
✅ **Engagement** - Announcements, surveys  
✅ **Compliance** - Violations tracking  
✅ **Service Desk** - Ticket management  
✅ **Documents** - File management system  

## 🚀 Quick Start

### Prerequisites

- Node.js 18+ and npm

### Run the Prototype

```bash
cd silingan-frontend-prototype

# Install dependencies (if not already done)
npm install

# Start development server
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) in your browser.

### Production Build

```bash
npm run build
npm start
```

## 📁 Project Structure

```
silingan-frontend-prototype/
├── app/                        # Next.js pages (App Router)
│   ├── page.tsx                # Root → redirects to /dashboard
│   ├── layout.tsx              # Root layout with AppShell
│   ├── dashboard/              # Dashboard with stats
│   ├── directory/staff/        # Staff Directory table
│   ├── admin/roles/            # Role & Permissions matrix
│   ├── operations/             # Operations management
│   ├── security/               # Security incidents
│   ├── billing/                # Billing & finance
│   ├── facilities/             # Assets management
│   ├── engagement/             # Community announcements
│   ├── compliance/             # Violations
│   ├── service-desk/           # Tickets
│   └── documents/              # Document management
├── components/
│   ├── layout/
│   │   └── app-shell.tsx       # Main navigation shell
│   ├── modals/
│   │   └── edit-staff-access.tsx  # Edit Staff modal
│   └── ui/                     # Reusable components
│       ├── button.tsx
│       ├── card.tsx
│       ├── input.tsx
│       ├── badge.tsx
│       ├── modal.tsx
│       └── toast.tsx
├── lib/
│   ├── mock-data/              # Mock API responses
│   └── utils.ts                # Helper functions
├── types/                      # TypeScript definitions
└── public/                     # Static assets
```

## 🎨 Design System

Based on wireframe specifications:
- **Primary Color**: Blue (#2563EB)
- **Border Radius**: 12px rounded corners
- **Shadows**: Soft shadows for depth
- **Typography**: System fonts, clear hierarchy
- **Layout**: Clean, minimalist, enterprise-grade

Inspired by: Linear, Stripe Dashboard, Notion, Airbnb Admin

## 📱 Pages Reference

| Route | Description | Key Features |
|-------|-------------|--------------|
| `/` | Root | Redirects to dashboard |
| `/dashboard` | Dashboard | Stats cards, charts, widgets |
| `/directory/staff` | Staff Directory | Search, filters, Edit Access |
| `/admin/roles` | Role & Access | Permission matrix editor |
| `/operations` | Operations | Tabs: Maintenance, Work Orders, etc. |
| `/security` | Security | Incident reports table |
| `/billing` | Billing | Bills, payments, statistics |
| `/facilities` | Facilities | Assets, parking, meters |
| `/engagement` | Engagement | Announcements, surveys |
| `/compliance` | Compliance | Violations tracking |
| `/service-desk` | Service Desk | Ticket management |
| `/documents` | Documents | File management |

## 🔧 Technology Stack

- **Framework**: Next.js 16.3+ (App Router with Turbopack)
- **Language**: TypeScript 5+
- **Styling**: Tailwind CSS
- **Icons**: Lucide React
- **Charts**: Recharts
- **State Management**: React hooks (client-side only)

## 🎯 Mock Data

All data is static and defined in `lib/mock-data/index.ts`:

- 3 communities (Silingan Tower A, Green Meadows, Urban Heights)
- 3 staff members with different roles
- Dashboard statistics
- Operations, incidents, billing records
- Assets, announcements, violations
- Tickets and documents

## 🚧 Limitations

**Frontend prototype only:**

❌ No backend API  
❌ No authentication  
❌ No data persistence  
❌ No real-time updates  
❌ Community switching updates UI only (no API call)  

All API endpoints from the wireframe are documented but not implemented.

## 📊 RBAC Implementation

Following wireframe section 15:

1. **Capabilities** loaded from mock data on app init
2. **Tabs hidden** when no `*_VIEW` permission
3. **Read-only mode** when only `*_VIEW` granted
4. **Action buttons** shown only with `*_MANAGE` permission
5. **Destructive actions** require `*_MANAGE` + confirmation

## 🎭 Interactive Features

✅ Community dropdown switcher  
✅ All navigation tabs functional  
✅ Edit Staff Access modal with role radio buttons  
✅ Permission matrix with checkboxes  
✅ Search and filter inputs  
✅ Action buttons show success toasts  
✅ Status badges with proper colors  
✅ Tab navigation in multi-view pages  

## 📝 Wireframe Mapping

**Section 2: Directory > Staff** → `/directory/staff`  
**Section 3: Edit Staff Access** → Modal component  
**Section 4: Admin > Role & Access** → `/admin/roles`  
**Section 6: Dashboard** → `/dashboard`  
**Section 7: Operations** → `/operations`  
**Section 8: Security** → `/security`  
**Section 9: Billing** → `/billing`  
**Section 10: Facilities** → `/facilities`  
**Section 11: Engagement** → `/engagement`  
**Section 12: Compliance** → `/compliance`  
**Section 13: Service Desk** → `/service-desk`  
**Section 14: Documents** → `/documents`  

## 🧪 Testing the Prototype

1. **Navigate tabs** - Click Dashboard, Directory, Operations, etc.
2. **Switch communities** - Use dropdown in header
3. **Edit staff access** - Go to Directory > Staff, click "Edit Access"
4. **Modify permissions** - Go to Admin, select role, change matrix
5. **Use filters** - Search boxes and dropdowns are functional
6. **Click actions** - Create, Add, Generate buttons show toasts

## 🔗 API Reference

See wireframe section 5 for complete API mapping:

```
GET  /api/v1/communities/{communityId}/staff-permissions/directory
PUT  /api/v1/communities/{communityId}/staff/{userId}/role
GET  /api/v1/communities/{communityId}/staff/{userId}/effective-permissions
GET  /api/v1/communities/{communityId}/rbac/roles
PUT  /api/v1/communities/{communityId}/rbac/roles/{roleCode}/permissions
GET  /api/v1/rbac/permissions
GET  /api/v1/me/communities/{communityId}/capabilities
```

## 📦 Build Info

```bash
# Development
npm run dev          # Start dev server with Turbopack

# Production
npm run build        # Build optimized production bundle
npm start            # Start production server

# Linting
npm run lint         # Run ESLint
```

**Build Status**: ✅ 14 routes, 0 errors, TypeScript passing

## 📄 License

Prototype for Silingan Community Platform

---

**Built with Next.js, TypeScript, and Tailwind CSS**  
Strictly following wireframe specifications from `COMMUNITY_RBAC_UI_WIREFRAME.md`
