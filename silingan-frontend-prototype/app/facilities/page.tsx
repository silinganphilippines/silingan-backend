'use client';

import { useState } from 'react';
import { AppShell } from '@/components/layout/app-shell';
import { mockCommunities, mockAssets } from '@/lib/mock-data';
import { Plus } from 'lucide-react';
import { cn } from '@/lib/utils';

export default function FacilitiesPage() {
  const [currentCommunity, setCurrentCommunity] = useState(mockCommunities[0]);
  const [activeTab, setActiveTab] = useState<string>('assets');

  const handleCommunityChange = (communityId: string) => {
    const community = mockCommunities.find((c) => c.id === communityId);
    if (community) setCurrentCommunity(community);
  };

  const tabs = [
    { id: 'assets', label: 'Assets' },
    { id: 'parking', label: 'Parking' },
    { id: 'utility-meters', label: 'Utility Meters' }
  ];

  const filteredAssets = mockAssets.filter((asset) => asset.tab === activeTab);

  return (
    <AppShell
      currentCommunity={currentCommunity}
      communities={mockCommunities}
      onCommunityChange={handleCommunityChange}
    >
      <div className="space-y-6">
        {/* Header */}
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Facilities</h1>
        </div>

        {/* Tabs */}
        <div className="border-b border-gray-200">
          <nav className="flex gap-1">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={cn(
                  'px-4 py-3 text-sm font-medium border-b-2 transition-colors',
                  activeTab === tab.id
                    ? 'border-blue-600 text-blue-600'
                    : 'border-transparent text-gray-600 hover:text-gray-900 hover:border-gray-300'
                )}
              >
                {tab.label}
              </button>
            ))}
          </nav>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="bg-white border border-gray-200 rounded-lg p-4">
            <p className="text-sm text-gray-600">Assets Active</p>
            <p className="text-2xl font-bold text-gray-900 mt-1">15</p>
          </div>
          <div className="bg-white border border-gray-200 rounded-lg p-4">
            <p className="text-sm text-gray-600">Parking Occupied</p>
            <p className="text-2xl font-bold text-gray-900 mt-1">42/50</p>
          </div>
          <div className="bg-white border border-gray-200 rounded-lg p-4">
            <p className="text-sm text-gray-600">Pending PM Tasks</p>
            <p className="text-2xl font-bold text-yellow-600 mt-1">3</p>
          </div>
        </div>

        {/* Table */}
        <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Code
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Asset Name
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Location
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Condition
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Last Service
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Status
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {filteredAssets.map((asset) => (
                <tr key={asset.code} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 text-sm font-medium text-gray-900">
                    {asset.code}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-600">{asset.assetName}</td>
                  <td className="px-6 py-4 text-sm text-gray-600">{asset.location}</td>
                  <td className="px-6 py-4 text-sm">
                    <span
                      className={cn(
                        'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                        asset.condition === 'Excellent' && 'bg-green-100 text-green-800',
                        asset.condition === 'Good' && 'bg-blue-100 text-blue-800',
                        asset.condition === 'Fair' && 'bg-yellow-100 text-yellow-800'
                      )}
                    >
                      {asset.condition}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-600">{asset.lastService}</td>
                  <td className="px-6 py-4 text-sm">
                    <span
                      className={cn(
                        'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                        asset.status === 'Active' && 'bg-green-100 text-green-800',
                        asset.status === 'Occupied' && 'bg-blue-100 text-blue-800',
                        asset.status === 'Inactive' && 'bg-gray-100 text-gray-800'
                      )}
                    >
                      {asset.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Add Asset Button */}
        <div className="flex justify-end">
          <button className="flex items-center gap-2 px-6 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors">
            Add Asset
            <Plus className="w-4 h-4" />
          </button>
        </div>
      </div>
    </AppShell>
  );
}
