'use client';

import { useState } from 'react';
import { AppShell } from '@/components/layout/app-shell';
import { mockCommunities, mockStaffMembers } from '@/lib/mock-data';
import { Search, Plus, Filter } from 'lucide-react';
import { EditStaffAccessModal } from '@/components/modals/edit-staff-access';
import { Badge } from '@/components/ui/badge';
import type { StaffMember } from '@/types';

export default function StaffDirectoryPage() {
  const [currentCommunity, setCurrentCommunity] = useState(mockCommunities[0]);
  const [searchQuery, setSearchQuery] = useState('');
  const [roleFilter, setRoleFilter] = useState('All');
  const [statusFilter, setStatusFilter] = useState('All');
  const [selectedStaff, setSelectedStaff] = useState<StaffMember | null>(null);
  const [showEditModal, setShowEditModal] = useState(false);

  const handleCommunityChange = (communityId: string) => {
    const community = mockCommunities.find((c) => c.id === communityId);
    if (community) setCurrentCommunity(community);
  };

  const handleEditAccess = (staff: StaffMember) => {
    setSelectedStaff(staff);
    setShowEditModal(true);
  };

  const filteredStaff = mockStaffMembers.filter((staff) => {
    const matchesSearch =
      staff.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      staff.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
      staff.mobile.includes(searchQuery);
    const matchesRole = roleFilter === 'All' || staff.role === roleFilter;
    const matchesStatus = statusFilter === 'All' || staff.status === statusFilter;
    return matchesSearch && matchesRole && matchesStatus;
  });

  return (
    <AppShell
      currentCommunity={currentCommunity}
      communities={mockCommunities}
      onCommunityChange={handleCommunityChange}
    >
      <div className="space-y-6">
        {/* Header */}
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-slate-900">Staff Directory</h1>
            <p className="text-slate-600 mt-1">{filteredStaff.length} staff members</p>
          </div>
          <button className="flex items-center gap-2 px-4 py-2.5 text-sm font-medium text-white stat-card-gradient rounded-lg hover:shadow-lg transition-all duration-200 transform hover:-translate-y-0.5">
            <Plus className="w-4 h-4" />
            Add Staff
          </button>
        </div>

        {/* Filters */}
        <div className="flex flex-wrap gap-4 bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
          <div className="flex-1 min-w-[240px] relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
            <input
              type="text"
              placeholder="Search name, email, or mobile..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-10 pr-4 py-2.5 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 text-sm"
            />
          </div>

          <div className="relative">
            <Filter className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
            <select
              value={roleFilter}
              onChange={(e) => setRoleFilter(e.target.value)}
              className="pl-9 pr-8 py-2.5 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent appearance-none bg-white cursor-pointer hover:bg-slate-50 transition-colors text-sm"
            >
              <option value="All">All Roles</option>
              <option value="PMO_STAFF">PMO Staff</option>
              <option value="SECURITY_ADMIN">Security Admin</option>
              <option value="MAINTENANCE_ADMIN">Maintenance Admin</option>
              <option value="READ_ONLY_STAFF">Read-Only Staff</option>
            </select>
          </div>

          <div className="relative">
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="px-4 pr-8 py-2.5 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent appearance-none bg-white cursor-pointer hover:bg-slate-50 transition-colors text-sm"
            >
              <option value="All">All Status</option>
              <option value="Active">Active</option>
              <option value="Inactive">Inactive</option>
            </select>
          </div>
        </div>

        {/* Table */}
        <div className="table-container">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="table-header">
                <tr>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Name
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Role
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Email
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Mobile
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-4 text-right text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {filteredStaff.map((staff, index) => (
                  <tr 
                    key={staff.id} 
                    className={`transition-all duration-150 hover:bg-blue-50/50 ${index % 2 === 0 ? 'bg-white' : 'bg-slate-50/30'}`}
                  >
                    <td className="px-6 py-4 text-sm font-medium text-slate-900">
                      {staff.name}
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-600">{staff.role}</td>
                    <td className="px-6 py-4 text-sm text-slate-600">{staff.email}</td>
                    <td className="px-6 py-4 text-sm text-slate-600">{staff.mobile}</td>
                    <td className="px-6 py-4 text-sm">
                      <Badge
                        variant={staff.status === 'Active' ? 'success' : 'default'}
                        dot
                      >
                        {staff.status}
                      </Badge>
                    </td>
                    <td className="px-6 py-4 text-sm text-right">
                      <button
                        onClick={() => handleEditAccess(staff)}
                        className="px-4 py-2 text-sm font-medium text-blue-600 hover:bg-blue-50 rounded-lg transition-all duration-150 border border-transparent hover:border-blue-200"
                      >
                        Edit Access
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {filteredStaff.length === 0 && (
          <div className="text-center py-12 bg-white border border-slate-200 rounded-xl">
            <Search className="w-12 h-12 text-slate-400 mx-auto mb-3" />
            <p className="text-slate-600 font-medium">No staff members found</p>
            <p className="text-slate-500 text-sm mt-1">Try adjusting your search or filters</p>
          </div>
        )}
      </div>

      {showEditModal && selectedStaff && (
        <EditStaffAccessModal
          staff={selectedStaff}
          community={currentCommunity}
          onClose={() => {
            setShowEditModal(false);
            setSelectedStaff(null);
          }}
          onSave={() => {
            setShowEditModal(false);
            setSelectedStaff(null);
            // In a real app, this would save to API
          }}
        />
      )}
    </AppShell>
  );
}
