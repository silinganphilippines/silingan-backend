'use client';

import { useState } from 'react';
import { AppShell } from '@/components/layout/app-shell';
import { mockCommunities, mockViolations } from '@/lib/mock-data';
import { Search } from 'lucide-react';
import { cn } from '@/lib/utils';

export default function CompliancePage() {
  const [currentCommunity, setCurrentCommunity] = useState(mockCommunities[0]);
  const [searchQuery, setSearchQuery] = useState('');

  const handleCommunityChange = (communityId: string) => {
    const community = mockCommunities.find((c) => c.id === communityId);
    if (community) setCurrentCommunity(community);
  };

  const filteredViolations = mockViolations.filter((violation) => {
    return (
      violation.ref.toLowerCase().includes(searchQuery.toLowerCase()) ||
      violation.unit.toLowerCase().includes(searchQuery.toLowerCase()) ||
      violation.violationType.toLowerCase().includes(searchQuery.toLowerCase()) ||
      violation.issuedTo.toLowerCase().includes(searchQuery.toLowerCase())
    );
  });

  return (
    <AppShell
      currentCommunity={currentCommunity}
      communities={mockCommunities}
      onCommunityChange={handleCommunityChange}
    >
      <div className="space-y-6">
        {/* Header */}
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Compliance</h1>
        </div>

        {/* Filters */}
        <div className="flex flex-wrap gap-4">
          <div className="flex-1 min-w-[240px] relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              type="text"
              placeholder="Search violations"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>

        {/* Table */}
        <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Ref
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Unit
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Violation Type
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Issued To
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Updated
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {filteredViolations.map((violation) => (
                <tr key={violation.ref} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 text-sm font-medium text-gray-900">
                    {violation.ref}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-600">{violation.unit}</td>
                  <td className="px-6 py-4 text-sm text-gray-600">{violation.violationType}</td>
                  <td className="px-6 py-4 text-sm text-gray-600">{violation.issuedTo}</td>
                  <td className="px-6 py-4 text-sm">
                    <span
                      className={cn(
                        'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                        violation.status === 'Open' && 'bg-yellow-100 text-yellow-800',
                        violation.status === 'Under Review' && 'bg-blue-100 text-blue-800',
                        violation.status === 'Resolved' && 'bg-green-100 text-green-800'
                      )}
                    >
                      {violation.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-600">{violation.updated}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </AppShell>
  );
}
