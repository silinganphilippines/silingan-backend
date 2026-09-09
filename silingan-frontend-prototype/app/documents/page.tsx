'use client';

import { useState } from 'react';
import { AppShell } from '@/components/layout/app-shell';
import { mockCommunities, mockDocuments } from '@/lib/mock-data';
import { Upload, FolderPlus } from 'lucide-react';
import { cn } from '@/lib/utils';

export default function DocumentsPage() {
  const [currentCommunity, setCurrentCommunity] = useState(mockCommunities[0]);
  const [activeTab, setActiveTab] = useState<string>('policies');

  const handleCommunityChange = (communityId: string) => {
    const community = mockCommunities.find((c) => c.id === communityId);
    if (community) setCurrentCommunity(community);
  };

  const tabs = [
    { id: 'policies', label: 'Policies' },
    { id: 'forms', label: 'Forms' },
    { id: 'contracts', label: 'Contracts/Permits' },
    { id: 'access-logs', label: 'Access Logs' }
  ];

  const filteredDocuments = mockDocuments.filter((doc) => doc.tab === activeTab);

  return (
    <AppShell
      currentCommunity={currentCommunity}
      communities={mockCommunities}
      onCommunityChange={handleCommunityChange}
    >
      <div className="space-y-6">
        {/* Header */}
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Documents</h1>
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

        {/* Table */}
        <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Name
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Version
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Updated
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Access
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {filteredDocuments.map((document) => (
                <tr key={document.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 text-sm font-medium text-gray-900">
                    {document.name}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-600">{document.version}</td>
                  <td className="px-6 py-4 text-sm text-gray-600">{document.updated}</td>
                  <td className="px-6 py-4 text-sm">
                    <span
                      className={cn(
                        'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                        document.access === 'Public' && 'bg-green-100 text-green-800',
                        document.access === 'Staff' && 'bg-blue-100 text-blue-800',
                        document.access === 'Private' && 'bg-gray-100 text-gray-800'
                      )}
                    >
                      {document.access}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Action Buttons */}
        <div className="flex gap-3">
          <button className="flex items-center gap-2 px-6 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors">
            <Upload className="w-4 h-4" />
            Upload
          </button>
          <button className="flex items-center gap-2 px-6 py-2 text-sm font-medium text-blue-600 bg-blue-50 rounded-lg hover:bg-blue-100 transition-colors">
            <FolderPlus className="w-4 h-4" />
            New Folder
          </button>
        </div>
      </div>
    </AppShell>
  );
}
