import {
  Community,
  StaffMember,
  Role,
  RolePermission,
  Operation,
  SecurityIncident,
  BillingRecord,
  Asset,
  Announcement,
  Violation,
  Ticket,
  Document
} from '@/types';

export const mockCommunities: Community[] = [
  { id: '1', name: 'Silingan Tower A', address: 'Makati City', type: 'Condominium' },
  { id: '2', name: 'Silingan Tower B', address: 'Makati City', type: 'Condominium' },
  { id: '3', name: 'Greenfield Village', address: 'Quezon City', type: 'Subdivision' }
];

export const mockStaffMembers: StaffMember[] = [
  {
    id: '1',
    name: 'Juan Dela Cruz',
    email: 'juan@mail.com',
    mobile: '0917-123-4567',
    role: 'PMO_STAFF',
    status: 'Active'
  },
  {
    id: '2',
    name: 'Maria Santos',
    email: 'maria@mail.com',
    mobile: '0918-234-5678',
    role: 'SECURITY_ADMIN',
    status: 'Active'
  },
  {
    id: '3',
    name: 'Leo Ramos',
    email: 'leo@mail.com',
    mobile: '0920-345-6789',
    role: 'READ_ONLY_STAFF',
    status: 'Inactive'
  },
  {
    id: '4',
    name: 'Anna Reyes',
    email: 'anna@mail.com',
    mobile: '0919-456-7890',
    role: 'MAINTENANCE_ADMIN',
    status: 'Active'
  }
];

export const mockRoles: Role[] = [
  { code: 'COMMUNITY_ADMIN', name: 'Community Admin', isCustomized: false },
  { code: 'PMO_STAFF', name: 'PMO Staff', isCustomized: true },
  { code: 'SECURITY_ADMIN', name: 'Security Admin', isCustomized: false },
  { code: 'MAINTENANCE_ADMIN', name: 'Maintenance Admin', isCustomized: false },
  { code: 'READ_ONLY_STAFF', name: 'Read-Only Staff', isCustomized: false }
];

export const mockRolePermissions: RolePermission[] = [
  { domain: 'Community', canView: false, canManage: false },
  { domain: 'Resident', canView: true, canManage: false },
  { domain: 'Staff', canView: true, canManage: false },
  { domain: 'Announcement', canView: true, canManage: true },
  { domain: 'Report', canView: true, canManage: true },
  { domain: 'Directory', canView: true, canManage: false },
  { domain: 'Dashboard', canView: true, canManage: false },
  { domain: 'Document', canView: true, canManage: false },
  { domain: 'Notification', canView: false, canManage: false }
];

export const mockOperations: Operation[] = [
  {
    id: 'OP001',
    type: 'Maintenance',
    requestor: 'Unit 12B',
    status: 'Open',
    priority: 'High',
    updated: '2026-08-29',
    tab: 'maintenance'
  },
  {
    id: 'OP002',
    type: 'Visitor Pass',
    requestor: 'Juan Cruz',
    status: 'Approved',
    priority: 'Normal',
    updated: '2026-08-29',
    tab: 'visitor-pass'
  },
  {
    id: 'OP003',
    type: 'Work Order',
    requestor: 'Admin',
    status: 'In Progress',
    priority: 'High',
    updated: '2026-08-28',
    tab: 'work-orders'
  },
  {
    id: 'OP004',
    type: 'Amenity Booking',
    requestor: 'Unit 5A',
    status: 'Confirmed',
    priority: 'Normal',
    updated: '2026-08-27',
    tab: 'amenities'
  }
];

export const mockSecurityIncidents: SecurityIncident[] = [
  {
    ref: 'IN001',
    incidentType: 'Unauthorized Entry',
    location: 'Gate 2',
    severity: 'High',
    status: 'Investigating',
    officer: 'A. Santos',
    date: '2026-08-29'
  },
  {
    ref: 'IN002',
    incidentType: 'Noise Complaint',
    location: 'Unit 8C',
    severity: 'Low',
    status: 'Resolved',
    officer: 'B. Cruz',
    date: '2026-08-28'
  },
  {
    ref: 'IN003',
    incidentType: 'Suspicious Activity',
    location: 'Parking Area',
    severity: 'Medium',
    status: 'Under Review',
    officer: 'A. Santos',
    date: '2026-08-27'
  }
];

export const mockBillingRecords: BillingRecord[] = [
  {
    unit: '12B',
    resident: 'Juan D. Cruz',
    billingPeriod: 'Aug 2026',
    amount: 5500.00,
    dueDate: '2026-09-05',
    status: 'Pending'
  },
  {
    unit: '5A',
    resident: 'Maria Santos',
    billingPeriod: 'Aug 2026',
    amount: 4800.00,
    dueDate: '2026-09-05',
    status: 'Paid'
  },
  {
    unit: '8C',
    resident: 'Leo Ramos',
    billingPeriod: 'Aug 2026',
    amount: 6200.00,
    dueDate: '2026-09-05',
    status: 'Overdue'
  }
];

export const mockAssets: Asset[] = [
  {
    code: 'AS001',
    assetName: 'Elevator A',
    location: 'Tower A',
    condition: 'Good',
    lastService: '2026-08-01',
    status: 'Active',
    tab: 'assets'
  },
  {
    code: 'AS002',
    assetName: 'Generator',
    location: 'Basement',
    condition: 'Excellent',
    lastService: '2026-07-15',
    status: 'Active',
    tab: 'assets'
  },
  {
    code: 'PK001',
    assetName: 'Slot A-101',
    location: 'Parking Level 1',
    condition: 'Good',
    lastService: 'N/A',
    status: 'Occupied',
    tab: 'parking'
  }
];

export const mockAnnouncements: Announcement[] = [
  {
    id: 'AN001',
    title: 'Water Service Advisory',
    audience: 'All Units',
    author: 'PMO Staff',
    date: '2026-08-29',
    status: 'Published'
  },
  {
    id: 'AN002',
    title: 'HOA Meeting Reminder',
    audience: 'All Owners',
    author: 'Community Admin',
    date: '2026-08-28',
    status: 'Published'
  },
  {
    id: 'AN003',
    title: 'Parking Policy Update',
    audience: 'All Units',
    author: 'PMO Staff',
    date: '2026-08-27',
    status: 'Draft'
  }
];

export const mockViolations: Violation[] = [
  {
    ref: 'CV001',
    unit: '10A',
    violationType: 'Noise Complaint',
    issuedTo: 'Resident User',
    status: 'Open',
    updated: '2026-08-29'
  },
  {
    ref: 'CV002',
    unit: '15B',
    violationType: 'Parking Violation',
    issuedTo: 'John Doe',
    status: 'Resolved',
    updated: '2026-08-28'
  },
  {
    ref: 'CV003',
    unit: '7C',
    violationType: 'Pet Policy Breach',
    issuedTo: 'Maria Garcia',
    status: 'Under Review',
    updated: '2026-08-27'
  }
];

export const mockTickets: Ticket[] = [
  {
    id: 'SD001',
    subject: 'Intercom Issue',
    requestor: 'Unit 9C',
    assignee: 'Tech Team',
    sla: '4h',
    status: 'In Progress'
  },
  {
    id: 'SD002',
    subject: 'Key Card Not Working',
    requestor: 'Unit 14A',
    assignee: 'Security Team',
    sla: '2h',
    status: 'Open'
  },
  {
    id: 'SD003',
    subject: 'Water Leak Report',
    requestor: 'Unit 3B',
    assignee: 'Maintenance Team',
    sla: '1h',
    status: 'Resolved'
  }
];

export const mockDocuments: Document[] = [
  {
    id: 'DOC001',
    name: 'House Rules',
    version: 'v3',
    updated: '2026-08-20',
    access: 'Public',
    tab: 'policies'
  },
  {
    id: 'DOC002',
    name: 'Move-in Form',
    version: 'v5',
    updated: '2026-08-15',
    access: 'Staff',
    tab: 'forms'
  },
  {
    id: 'DOC003',
    name: 'Parking Agreement',
    version: 'v2',
    updated: '2026-08-10',
    access: 'Private',
    tab: 'contracts'
  },
  {
    id: 'DOC004',
    name: 'Building Permit',
    version: 'v1',
    updated: '2026-07-25',
    access: 'Staff',
    tab: 'contracts'
  }
];

export const mockEffectivePermissions = [
  'DASHBOARD_VIEW',
  'ANNOUNCEMENT_VIEW',
  'ANNOUNCEMENT_MANAGE',
  'REPORT_VIEW',
  'REPORT_MANAGE',
  'RESIDENT_VIEW',
  'STAFF_VIEW',
  'DIRECTORY_VIEW',
  'DOCUMENT_VIEW'
];
