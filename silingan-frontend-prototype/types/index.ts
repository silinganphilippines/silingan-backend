export interface Community {
  id: string;
  name: string;
  address?: string;
  type?: string;
}

export interface StaffMember {
  id: string;
  name: string;
  email: string;
  mobile: string;
  role: string;
  status: 'Active' | 'Inactive';
}

export interface Role {
  code: string;
  name: string;
  isCustomized?: boolean;
}

export interface Permission {
  code: string;
  name: string;
  domain: string;
  action: 'VIEW' | 'MANAGE';
}

export interface RolePermission {
  domain: string;
  canView: boolean;
  canManage: boolean;
}

export interface Operation {
  id: string;
  type: string;
  requestor: string;
  status: string;
  priority: string;
  updated: string;
  tab: 'maintenance' | 'work-orders' | 'amenities' | 'visitor-pass' | 'move-requests';
}

export interface SecurityIncident {
  ref: string;
  incidentType: string;
  location: string;
  severity: string;
  status: string;
  officer: string;
  date: string;
}

export interface BillingRecord {
  unit: string;
  resident: string;
  billingPeriod: string;
  amount: number;
  dueDate: string;
  status: string;
}

export interface Asset {
  code: string;
  assetName: string;
  location: string;
  condition: string;
  lastService: string;
  status: string;
  tab: 'assets' | 'parking' | 'utility-meters';
}

export interface Announcement {
  id: string;
  title: string;
  audience: string;
  author: string;
  date: string;
  status: string;
}

export interface Violation {
  ref: string;
  unit: string;
  violationType: string;
  issuedTo: string;
  status: string;
  updated: string;
}

export interface Ticket {
  id: string;
  subject: string;
  requestor: string;
  assignee: string;
  sla: string;
  status: string;
}

export interface Document {
  id: string;
  name: string;
  version: string;
  updated: string;
  access: string;
  tab: 'policies' | 'forms' | 'contracts' | 'access-logs';
}
