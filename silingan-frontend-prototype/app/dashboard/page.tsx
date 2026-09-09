'use client';

import { useState } from 'react';
import { AppShell } from '@/components/layout/app-shell';
import { mockCommunities } from '@/lib/mock-data';
import { Card } from '@/components/ui/card';
import { Users, AlertTriangle, FileText, Building, TrendingUp, Clock } from 'lucide-react';

export default function DashboardPage() {
  const [currentCommunity, setCurrentCommunity] = useState(mockCommunities[0]);

  const handleCommunityChange = (communityId: string) => {
    const community = mockCommunities.find((c) => c.id === communityId);
    if (community) setCurrentCommunity(community);
  };

  const stats = [
    {
      title: 'Open Tickets',
      value: '12',
      icon: FileText,
      gradient: 'stat-card-gradient',
      change: '+2 from yesterday'
    },
    {
      title: 'Open Incidents',
      value: '3',
      icon: AlertTriangle,
      gradient: 'danger-gradient',
      change: '-1 from yesterday'
    },
    {
      title: 'Unpaid Bills',
      value: '8',
      icon: FileText,
      gradient: 'warning-gradient',
      change: '+3 this week'
    },
    {
      title: 'Occupancy Rate',
      value: '92%',
      icon: Building,
      gradient: 'success-gradient',
      change: '+2% this month'
    }
  ];

  const recentActivity = [
    { id: 1, text: 'New ticket created: Water leak in Unit 12B', time: '5 min ago', type: 'ticket' },
    { id: 2, text: 'Payment received from Unit 8A', time: '15 min ago', type: 'payment' },
    { id: 3, text: 'Security incident reported at Gate 2', time: '1 hour ago', type: 'incident' },
    { id: 4, text: 'Maintenance completed: Elevator B', time: '2 hours ago', type: 'maintenance' }
  ];

  return (
    <AppShell
      currentCommunity={currentCommunity}
      communities={mockCommunities}
      onCommunityChange={handleCommunityChange}
    >
      <div className="space-y-6">
        {/* Header */}
        <div>
          <h1 className="text-3xl font-bold text-slate-900">Dashboard</h1>
          <p className="text-slate-600 mt-1">Welcome back! Here's what's happening in {currentCommunity.name}</p>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {stats.map((stat) => (
            <Card key={stat.title} className="relative overflow-hidden">
              <div className="p-6">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <p className="text-sm font-medium text-slate-600 mb-1">{stat.title}</p>
                    <p className="text-3xl font-bold text-slate-900 mb-2">{stat.value}</p>
                    <p className="text-xs text-slate-500">{stat.change}</p>
                  </div>
                  <div className={`p-3 rounded-xl ${stat.gradient} shadow-lg`}>
                    <stat.icon className="w-6 h-6 text-white" />
                  </div>
                </div>
              </div>
            </Card>
          ))}
        </div>

        {/* Charts Section */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <Card className="p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-slate-900">Issue Trend</h3>
              <TrendingUp className="w-5 h-5 text-slate-400" />
            </div>
            <div className="h-64 flex items-center justify-center border-2 border-dashed border-slate-300 rounded-xl bg-slate-50/50">
              <div className="text-center">
                <FileText className="w-12 h-12 text-slate-400 mx-auto mb-2" />
                <p className="text-slate-500 text-sm">Chart Placeholder</p>
                <p className="text-slate-400 text-xs">Last 30 days</p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-slate-900">Collection Trend</h3>
              <TrendingUp className="w-5 h-5 text-slate-400" />
            </div>
            <div className="h-64 flex items-center justify-center border-2 border-dashed border-slate-300 rounded-xl bg-slate-50/50">
              <div className="text-center">
                <FileText className="w-12 h-12 text-slate-400 mx-auto mb-2" />
                <p className="text-slate-500 text-sm">Chart Placeholder</p>
                <p className="text-slate-400 text-xs">Last 30 days</p>
              </div>
            </div>
          </Card>
        </div>

        {/* Bottom Widgets */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <Card className="p-6">
            <h3 className="text-lg font-semibold text-slate-900 mb-4 flex items-center gap-2">
              <Clock className="w-5 h-5 text-slate-500" />
              Recent Activity
            </h3>
            <div className="space-y-4">
              {recentActivity.map((activity) => (
                <div key={activity.id} className="flex items-start gap-3 group">
                  <div className="w-2 h-2 rounded-full bg-blue-500 mt-2 flex-shrink-0"></div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-slate-900 group-hover:text-blue-600 transition-colors">
                      {activity.text}
                    </p>
                    <p className="text-xs text-slate-500">{activity.time}</p>
                  </div>
                </div>
              ))}
            </div>
          </Card>

          <Card className="p-6">
            <h3 className="text-lg font-semibold text-slate-900 mb-4">Pending Approvals</h3>
            <div className="space-y-3">
              <div className="p-3 rounded-lg bg-amber-50 border border-amber-200 hover:bg-amber-100 transition-colors cursor-pointer">
                <p className="text-sm font-medium text-slate-900">Move Request - Unit 12B</p>
                <p className="text-xs text-slate-600 mt-1">Pending review</p>
              </div>
              <div className="p-3 rounded-lg bg-amber-50 border border-amber-200 hover:bg-amber-100 transition-colors cursor-pointer">
                <p className="text-sm font-medium text-slate-900">Visitor Pass - Juan C.</p>
                <p className="text-xs text-slate-600 mt-1">Awaiting approval</p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <h3 className="text-lg font-semibold text-slate-900 mb-4">Quick Actions</h3>
            <div className="space-y-2">
              <button className="w-full px-4 py-3 text-sm font-medium text-white stat-card-gradient rounded-lg hover:shadow-lg transition-all duration-200 transform hover:-translate-y-0.5">
                Create Announcement
              </button>
              <button className="w-full px-4 py-3 text-sm font-medium text-blue-600 bg-blue-50 border border-blue-200 rounded-lg hover:bg-blue-100 transition-all duration-200">
                Generate Report
              </button>
              <button className="w-full px-4 py-3 text-sm font-medium text-slate-600 bg-slate-50 border border-slate-200 rounded-lg hover:bg-slate-100 transition-all duration-200">
                View All Tasks
              </button>
            </div>
          </Card>
        </div>
      </div>
    </AppShell>
  );
}
