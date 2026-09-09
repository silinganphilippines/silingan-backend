'use client';

import { useState } from 'react';
import { X } from 'lucide-react';
import { mockRoles, mockEffectivePermissions } from '@/lib/mock-data';
import type { StaffMember, Community } from '@/types';

interface EditStaffAccessModalProps {
  staff: StaffMember;
  community: Community;
  onClose: () => void;
  onSave: () => void;
}

export function EditStaffAccessModal({
  staff,
  community,
  onClose,
  onSave
}: EditStaffAccessModalProps) {
  const [selectedRole, setSelectedRole] = useState(staff.role);
  const [accountStatus, setAccountStatus] = useState(staff.status);

  const handleSave = () => {
    // In a real app, this would call an API to save the changes
    onSave();
  };

  return (
    <>
      {/* Backdrop - Lighter and more subtle */}
      <div
        className="fixed inset-0 bg-slate-900/30 backdrop-blur-sm z-40 transition-opacity"
        onClick={onClose}
      />

      {/* Modal */}
      <div className="fixed inset-y-0 right-0 w-full max-w-2xl bg-white shadow-2xl z-50 overflow-y-auto">
        <div className="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
          <h2 className="text-xl font-bold text-gray-900">Edit Access</h2>
          <button
            onClick={onClose}
            className="p-2 text-gray-500 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="p-6 space-y-6">
          {/* Staff Info */}
          <div>
            <p className="text-sm text-gray-600">Staff</p>
            <p className="text-lg font-semibold text-gray-900">{staff.name}</p>
            <p className="text-sm text-gray-600 mt-1">Community: {community.name}</p>
          </div>

          {/* Assigned Role */}
          <div>
            <label className="block text-sm font-medium text-gray-900 mb-3">
              Assigned Role <span className="text-red-600">*</span>
            </label>
            <div className="space-y-2">
              {mockRoles.map((role) => (
                <label
                  key={role.code}
                  className="flex items-center gap-3 p-3 border border-gray-200 rounded-lg hover:bg-gray-50 cursor-pointer transition-colors"
                >
                  <input
                    type="radio"
                    name="role"
                    value={role.code}
                    checked={selectedRole === role.code}
                    onChange={(e) => setSelectedRole(e.target.value)}
                    className="w-4 h-4 text-blue-600 focus:ring-blue-500"
                  />
                  <span className="text-sm font-medium text-gray-900">
                    {role.name}
                  </span>
                </label>
              ))}
            </div>
          </div>

          {/* Account Status */}
          <div>
            <label htmlFor="status" className="block text-sm font-medium text-gray-900 mb-2">
              Account Status
            </label>
            <select
              id="status"
              value={accountStatus}
              onChange={(e) => setAccountStatus(e.target.value as 'Active' | 'Inactive')}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="Active">Active</option>
              <option value="Inactive">Inactive</option>
            </select>
          </div>

          {/* Effective Permissions */}
          <div>
            <h3 className="text-sm font-medium text-gray-900 mb-3">
              Effective Permissions (read-only)
            </h3>
            <div className="bg-gray-50 border border-gray-200 rounded-lg p-4">
              <ul className="space-y-2">
                {mockEffectivePermissions.map((permission) => (
                  <li key={permission} className="flex items-center gap-2 text-sm text-gray-700">
                    <span className="w-1.5 h-1.5 bg-gray-400 rounded-full" />
                    {permission}
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </div>

        {/* Footer Actions */}
        <div className="sticky bottom-0 bg-white border-t border-gray-200 px-6 py-4 flex items-center justify-end gap-3">
          <button
            onClick={onClose}
            className="px-6 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
          >
            Cancel
          </button>
          <button
            onClick={handleSave}
            className="px-6 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors"
          >
            Save Access
          </button>
        </div>
      </div>
    </>
  );
}
