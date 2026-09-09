'use client';

import { useState } from 'react';
import { AppShell } from '@/components/layout/app-shell';
import { mockCommunities, mockTickets } from '@/lib/mock-data';
import { Search, Plus } from 'lucide-react';
import { cn } from '@/lib/utils';

export default function ServiceDeskPage() {
  const [currentCommunity, setCurrentCommunity] = useState(mockCommunities[0]);
  const [searchQuery, setSearchQuery] = useState('');

  const handleCommunityChange = (communityId: string) => {
    const community = mockCommunities.find((c) => c.id === communityId);
    if (community) setCurrentCommunity(community);
  };

  const filteredTickets = mockTickets.filter((ticket) => {
    return (
      ticket.id.toLowerCase().includes(searchQuery.toLowerCase()) ||
      ticket.subject.toLowerCase().includes(searchQuery.toLowerCase()) ||
      ticket.requestor.toLowerCase().includes(searchQuery.toLowerCase()) ||
      ticket.assignee.toLowerCase().includes(searchQuery.toLowerCase())
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
          <h1 className="text-2xl font-bold text-gray-900">Service Desk</h1>
        </div>

        {/* Filters */}
        <div className="flex flex-wrap gap-4">
          <div className="flex-1 min-w-[240px] relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              type="text"
              placeholder="Search tickets"
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
                  Ticket
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Subject
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Requestor
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Assignee
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  SLA
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider">
                  Status
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {filteredTickets.map((ticket) => (
                <tr key={ticket.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 text-sm font-medium text-gray-900">
                    {ticket.id}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-600">{ticket.subject}</td>
                  <td className="px-6 py-4 text-sm text-gray-600">{ticket.requestor}</td>
                  <td className="px-6 py-4 text-sm text-gray-600">{ticket.assignee}</td>
                  <td className="px-6 py-4 text-sm text-gray-600">{ticket.sla}</td>
                  <td className="px-6 py-4 text-sm">
                    <span
                      className={cn(
                        'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                        ticket.status === 'Open' && 'bg-yellow-100 text-yellow-800',
                        ticket.status === 'In Progress' && 'bg-blue-100 text-blue-800',
                        ticket.status === 'Resolved' && 'bg-green-100 text-green-800'
                      )}
                    >
                      {ticket.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Create Ticket Button */}
        <div className="flex justify-end">
          <button className="flex items-center gap-2 px-6 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors">
            Create Ticket
            <Plus className="w-4 h-4" />
          </button>
        </div>
      </div>
    </AppShell>
  );
}
