'use client';

import { useState } from 'react';
import { AppShell } from '@/components/layout/app-shell';
import { mockCommunities, mockBillingRecords } from '@/lib/mock-data';
import { Badge } from '@/components/ui/badge';
import { Card } from '@/components/ui/card';
import { DollarSign, Calendar, TrendingUp, AlertCircle } from 'lucide-react';

export default function BillingPage() {
  const [currentCommunity, setCurrentCommunity] = useState(mockCommunities[0]);

  const handleCommunityChange = (communityId: string) => {
    const community = mockCommunities.find((c) => c.id === communityId);
    if (community) setCurrentCommunity(community);
  };

  return (
    <AppShell
      currentCommunity={currentCommunity}
      communities={mockCommunities}
      onCommunityChange={handleCommunityChange}
    >
      <div className="space-y-6">
        {/* Header */}
        <div>
          <h1 className="text-3xl font-bold text-slate-900">Billing & Finance</h1>
          <p className="text-slate-600 mt-1">Manage billing records and payments</p>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <Card className="p-5">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-sm font-medium text-slate-600">Due Today</p>
                <p className="text-3xl font-bold text-slate-900 mt-2">2</p>
              </div>
              <div className="p-3 rounded-xl bg-blue-100">
                <Calendar className="w-5 h-5 text-blue-600" />
              </div>
            </div>
          </Card>
          <Card className="p-5">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-sm font-medium text-slate-600">Overdue</p>
                <p className="text-3xl font-bold text-red-600 mt-2">1</p>
              </div>
              <div className="p-3 rounded-xl bg-red-100">
                <AlertCircle className="w-5 h-5 text-red-600" />
              </div>
            </div>
          </Card>
          <Card className="p-5">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-sm font-medium text-slate-600">Collected MTD</p>
                <p className="text-3xl font-bold text-emerald-600 mt-2">₱4,800</p>
              </div>
              <div className="p-3 rounded-xl bg-emerald-100">
                <DollarSign className="w-5 h-5 text-emerald-600" />
              </div>
            </div>
          </Card>
          <Card className="p-5">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-sm font-medium text-slate-600">Collection Rate</p>
                <p className="text-3xl font-bold text-slate-900 mt-2">33%</p>
              </div>
              <div className="p-3 rounded-xl bg-slate-100">
                <TrendingUp className="w-5 h-5 text-slate-600" />
              </div>
            </div>
          </Card>
        </div>

        {/* Table */}
        <div className="table-container">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="table-header">
                <tr>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Unit
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Resident
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Billing Period
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Amount
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Due Date
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-600 uppercase tracking-wider">
                    Status
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {mockBillingRecords.map((record, idx) => (
                  <tr 
                    key={idx} 
                    className={`transition-all duration-150 hover:bg-blue-50/50 ${idx % 2 === 0 ? 'bg-white' : 'bg-slate-50/30'}`}
                  >
                    <td className="px-6 py-4 text-sm font-medium text-slate-900">
                      {record.unit}
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-600">{record.resident}</td>
                    <td className="px-6 py-4 text-sm text-slate-600">{record.billingPeriod}</td>
                    <td className="px-6 py-4 text-sm text-slate-900 font-semibold">
                      ₱{record.amount.toLocaleString('en-PH', { minimumFractionDigits: 2 })}
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-600">{record.dueDate}</td>
                    <td className="px-6 py-4 text-sm">
                      <Badge
                        variant={
                          record.status === 'Pending' ? 'warning' :
                          record.status === 'Paid' ? 'success' : 'danger'
                        }
                        dot
                      >
                        {record.status}
                      </Badge>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex gap-3">
          <button className="px-6 py-2.5 text-sm font-medium text-white stat-card-gradient rounded-lg hover:shadow-lg transition-all duration-200 transform hover:-translate-y-0.5">
            Generate Bill
          </button>
          <button className="px-6 py-2.5 text-sm font-medium text-blue-600 bg-blue-50 border border-blue-200 rounded-lg hover:bg-blue-100 transition-all duration-200">
            Record Payment
          </button>
        </div>
      </div>
    </AppShell>
  );
}
