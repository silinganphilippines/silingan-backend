'use client';

import { useState } from 'react';
import { AppShell } from '@/components/layout/app-shell';
import { mockCommunities, mockRoles, mockRolePermissions } from '@/lib/mock-data';
import type { RolePermission } from '@/types';

export default function RolesPage() {
  const [currentCommunity, setCurrentCommunity] = useState(mockCommunities[0]);
  const [selectedRole, setSelectedRole] = useState('PMO_STAFF');
  const [permissions, setPermissions] = useState<RolePermission[]>(mockRolePermissions);

  const handleCommunityChange = (communityId: string) => {
    const community = mockCommunities.find((c) => c.id === communityId);
    if (community) setCurrentCommunity(community);
  };

  const togglePermission = (domain: string, type: 'view' | 'manage') => {
    setPermissions((prev) =>
      prev.map((perm) =>
        perm.domain === domain
          ? {
              ...perm,
              ...(type === 'view' ? { canView: !perm.canView } : { canManage: !perm.canManage })
            }
          : perm
      )
    );
  };

  const handleReset = () => {
    setPermissions(mockRolePermissions);
  };

  const handleSave = () => {
    // In a real app, this would call an API to save the changes
    console.log('Saving permissions:', permissions);
  };

  const currentRoleData = mockRoles.find((r) => r.code === selectedRole);

  return (
    <AppShell
      currentCommunity={currentCommunity}
      communities={mockCommunities}
      onCommunityChange={handleCommunityChange}
    >
      <div className="space-y-6">
        {/* Header */}
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Role & Access</h1>
        </div>

        {/* Role Selector */}
        <div className="flex items-center gap-4">
          <label htmlFor="role" className="text-sm font-medium text-gray-900">
            Role
          </label>
          <select
            id="role"
            value={selectedRole}
            onChange={(e) => setSelectedRole(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            {mockRoles.map((role) => (
              <option key={role.code} value={role.code}>
                {role.name}
              </option>
            ))}
          </select>
          {currentRoleData?.isCustomized && (
            <span className="inline-flex px-3 py-1 text-xs font-semibold bg-yellow-100 text-yellow-800 rounded-full">
              Customized
            </span>
          )}
        </div>

        {/* Permissions Matrix */}
        <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
          <div className="px-6 py-4 border-b border-gray-200 bg-gray-50">
            <h2 className="text-lg font-semibold text-gray-900">
              Permissions Matrix (Community Effective)
            </h2>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                    Domain
                  </th>
                  <th className="px-6 py-3 text-center text-xs font-medium text-gray-600 uppercase tracking-wider">
                    View
                  </th>
                  <th className="px-6 py-3 text-center text-xs font-medium text-gray-600 uppercase tracking-wider">
                    Manage
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {permissions.map((perm) => (
                  <tr key={perm.domain} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 text-sm font-medium text-gray-900">
                      {perm.domain}
                    </td>
                    <td className="px-6 py-4 text-center">
                      <input
                        type="checkbox"
                        checked={perm.canView}
                        onChange={() => togglePermission(perm.domain, 'view')}
                        className="w-4 h-4 text-blue-600 rounded focus:ring-blue-500"
                      />
                    </td>
                    <td className="px-6 py-4 text-center">
                      {perm.domain !== 'Dashboard' && (
                        <input
                          type="checkbox"
                          checked={perm.canManage}
                          onChange={() => togglePermission(perm.domain, 'manage')}
                          className="w-4 h-4 text-blue-600 rounded focus:ring-blue-500"
                        />
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Actions */}
        <div className="flex items-center justify-between">
          <button
            onClick={handleReset}
            className="px-6 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
          >
            Reset to Default
          </button>
          <button
            onClick={handleSave}
            className="px-6 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors"
          >
            Save Mapping
          </button>
        </div>
      </div>
    </AppShell>
  );
}
