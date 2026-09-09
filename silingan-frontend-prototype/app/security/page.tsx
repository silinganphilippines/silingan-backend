'use client';

import { useState } from 'react';
import { AppShell } from '@/components/layout/app-shell';
import { mockCommunities, mockSecurityIncidents } from '@/lib/mock-data';
import { Search, Shield, AlertTriangle } from 'lucide-react';
import { Badge } from '@/components/ui/badge';

export default function SecurityPage() {
  const [currentCommunity, setCurrentCommunity] = useState(mockCommunities[0]);
  const [searchQuery, setSearchQuery] = useState('');
  const [severityFilter, setSeverityFilter] = useState('All');
  const [statusFilter, setStatusFilter] = useState('All');

  const handleCommunityChange = (communityId: string) => {
    const community = mockCommunities.find((c) => c.id === communityId);
    if (community) setCurrentCommunity(community);
  };

  const filteredIncidents = mockSecurityIncidents.filter((incident) => {
    const matchesSearch =
      incident.ref.toLowerCase().includes(searchQuery.toLowerCase()) ||
      incident.incidentType.toLowerCase().includes(searchQuery.toLowerCase()) ||
      incident.location.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesSeverity = severityFilter === 'All' || incident.severity === severityFilter;
    const matchesStatus = statusFilter === 'All' || incident.status === statusFilter;
    return matchesSearch && matchesSeverity && matchesStatus;
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
          <h1 className="text-3xl font-bold text-slate-900 flex items-center gap-3">
            <Shield className="w-8 h-8 text-blue-600" />
            Security
          </h1>
          <p className="text-slate-600 mt-1">Monitor and manage security incidents</p>
        </div>

        {/* Filters */}
        <div className="flex flex-wrap gap-4 bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
          <div className="relative">
            <AlertTriangle className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
            <select
              value={severityFilter}
              onChange={(e) => setSeverityFilter(e.target.value)}
              className="pl-9 pr-8 py-2.5 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent appearance-none bg-white cursor-pointer hover:bg-slate-50 transition-colors text-sm"
            >
              <option value="All">All Severity</option>
              <option value="High">High</option>
              <option value="Medium">Medium</option>
              <option value="Low">Low</option>
            </select>
          </div>

          <div className="relative">
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="px-4 pr-8 py-2.5 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent appearance-none bg-white cursor-pointer hover:bg-slate-50 transition-colors text-sm"
            >
              <option value="All">All Status</option>
              <option value="Investigating">Investigating</option>
              <option value="Under Review">Under Review</option>
              <option value="Resolved">Resolved</option>
            </select>
          </div>

          <div className="flex-1 min-w-[240px] relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
            <input
              type="text"
              placeholder="Search incidents..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-10 pr-4 py-2.5 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 text-sm"
            />
          </div>
        </div>

        {/* Table */}
        <div className="table-container">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="table-header">
                <tr>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Ref
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Incident Type
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Location
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Severity
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Officer
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {filteredIncidents.map((incident, index) => (
                  <tr 
                    key={incident.ref}
                    className={`transition-all duration-150 hover:bg-blue-50/50 ${index % 2 === 0 ? 'bg-white' : 'bg-slate-50/30'}`}
                  >
                    <td className="px-6 py-4 text-sm font-medium text-slate-900">
                      {incident.ref}
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-600">{incident.incidentType}</td>
                    <td className="px-6 py-4 text-sm text-slate-600">{incident.location}</td>
                    <td className="px-6 py-4 text-sm">
                      <Badge
                        variant={
                          incident.severity === 'High' ? 'danger' :
                          incident.severity === 'Medium' ? 'warning' : 'info'
                        }
                        dot
                      >
                        {incident.severity}
                      </Badge>
                    </td>
                    <td className="px-6 py-4 text-sm">
                      <Badge
                        variant={
                          incident.status === 'Investigating' ? 'warning' :
                          incident.status === 'Under Review' ? 'info' : 'success'
                        }
                        dot
                      >
                        {incident.status}
                      </Badge>
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-600">{incident.officer}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {filteredIncidents.length === 0 && (
          <div className="text-center py-12 bg-white border border-slate-200 rounded-xl">
            <Shield className="w-12 h-12 text-slate-400 mx-auto mb-3" />
            <p className="text-slate-600 font-medium">No incidents found</p>
            <p className="text-slate-500 text-sm mt-1">Try adjusting your search or filters</p>
          </div>
        )}
      </div>
    </AppShell>
  );
}
