'use client';

import { useState } from 'react';
import { AppShell } from '@/components/layout/app-shell';
import { mockCommunities, mockOperations } from '@/lib/mock-data';
import { Search, Plus } from 'lucide-react';
import { cn } from '@/lib/utils';

export default function OperationsPage() {
  const [currentCommunity, setCurrentCommunity] = useState(mockCommunities[0]);
  const [activeTab, setActiveTab] = useState<string>('maintenance');
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('All');
  const [priorityFilter, setPriorityFilter] = useState('All');

  const handleCommunityChange = (communityId: string) => {
    const community = mockCommunities.find((c) => c.id === communityId);
    if (community) setCurrentCommunity(community);
  };

  const tabs = [
    { id: 'maintenance', label: 'Maintenance' },
    { id: 'work-orders', label: 'Work Orders' },
    { id: 'amenities', label: 'Amenities' },
    { id: 'visitor-pass', label: 'Visitor Pass' },
    { id: 'move-requests', label: 'Move Requests' }
  ];

  const filteredOperations = mockOperations.filter((op) => {
    const matchesTab = op.tab === activeTab;
    const matchesSearch =
      op.id.toLowerCase().includes(searchQuery.toLowerCase()) ||
      op.type.toLowerCase().includes(searchQuery.toLowerCase()) ||
      op.requestor.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus = statusFilter === 'All' || op.status === statusFilter;
    const matchesPriority = priorityFilter === 'All' || op.priority === priorityFilter;
    return matchesTab && matchesSearch && matchesStatus && matchesPriority;
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
          <h1 className="text-2xl font-bold text-gray-900">Operations</h1>
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

        {/* Filters */}
        <div className="flex flex-wrap gap-4">
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="All">All Status</option>
            <option value="Open">Open</option>
            <option value="In Progress">In Progress</option>
            <option value="Approved">Approved</option>
            <option value="Confirmed">Confirmed</option>
          </select>

          <select
            value={priorityFilter}
            onChange={(e) => setPriorityFilter(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="All">All Priority</option>
            <option value="High">High</option>
            <option value="Normal">Normal</option>
            <option value="Low">Low</option>
          </select>

          <div className="flex-1 min-w-[240px] relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              type="text"
              placeholder="Search operations"
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
                  ID
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Type
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Requestor
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Priority
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Updated
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {filteredOperations.map((operation) => (
                <tr key={operation.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 text-sm font-medium text-gray-900">
                    {operation.id}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-600">{operation.type}</td>
                  <td className="px-6 py-4 text-sm text-gray-600">{operation.requestor}</td>
                  <td className="px-6 py-4 text-sm">
                    <span
                      className={cn(
                        'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                        operation.status === 'Open' && 'bg-yellow-100 text-yellow-800',
                        operation.status === 'In Progress' && 'bg-blue-100 text-blue-800',
                        operation.status === 'Approved' && 'bg-green-100 text-green-800',
                        operation.status === 'Confirmed' && 'bg-green-100 text-green-800'
                      )}
                    >
                      {operation.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm">
                    <span
                      className={cn(
                        'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                        operation.priority === 'High' && 'bg-red-100 text-red-800',
                        operation.priority === 'Normal' && 'bg-gray-100 text-gray-800',
                        operation.priority === 'Low' && 'bg-blue-100 text-blue-800'
                      )}
                    >
                      {operation.priority}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-600">{operation.updated}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Create New Button */}
        <div className="flex justify-end">
          <button className="flex items-center gap-2 px-6 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors">
            Create New
            <Plus className="w-4 h-4" />
          </button>
        </div>
      </div>
    </AppShell>
  );
}
