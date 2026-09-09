'use client';

import { useState } from 'react';
import { ChevronDown, Bell } from 'lucide-react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { cn } from '@/lib/utils';
import { Community } from '@/types';

interface AppShellProps {
  children: React.ReactNode;
  currentCommunity: Community;
  communities: Community[];
  onCommunityChange: (communityId: string) => void;
}

export function AppShell({
  children,
  currentCommunity,
  communities,
  onCommunityChange
}: AppShellProps) {
  const pathname = usePathname();
  const [showCommunityDropdown, setShowCommunityDropdown] = useState(false);
  const [showUserDropdown, setShowUserDropdown] = useState(false);

  const navigation = [
    { name: 'Dashboard', href: '/dashboard' },
    { name: 'Directory', href: '/directory/staff' },
    { name: 'Operations', href: '/operations' },
    { name: 'Security', href: '/security' },
    { name: 'Billing', href: '/billing' },
    { name: 'Documents', href: '/documents' },
    { name: 'Admin', href: '/admin/roles' }
  ];

  return (
    <div className="min-h-screen bg-slate-50">
      {/* Header */}
      <header className="bg-gradient-to-b from-white to-slate-50/50 border-b border-slate-200 shadow-sm">
        <div className="px-6 py-3">
          <div className="flex items-center justify-between">
            {/* Community Selector */}
            <div className="relative">
              <button
                onClick={() => setShowCommunityDropdown(!showCommunityDropdown)}
                className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-slate-900 hover:bg-white rounded-lg transition-all duration-200 hover:shadow-sm"
              >
                <span>Community: {currentCommunity.name}</span>
                <ChevronDown className={cn(
                  "w-4 h-4 text-slate-500 transition-transform duration-200",
                  showCommunityDropdown && "rotate-180"
                )} />
              </button>

              {showCommunityDropdown && (
                <>
                  <div
                    className="fixed inset-0 z-10"
                    onClick={() => setShowCommunityDropdown(false)}
                  />
                  <div className="absolute left-0 mt-2 w-64 bg-white rounded-xl shadow-lg border border-slate-200 py-1 z-20 animate-in fade-in slide-in-from-top-2 duration-200">
                    {communities.map((community) => (
                      <button
                        key={community.id}
                        onClick={() => {
                          onCommunityChange(community.id);
                          setShowCommunityDropdown(false);
                        }}
                        className={cn(
                          'w-full px-4 py-2.5 text-left text-sm transition-all duration-150',
                          community.id === currentCommunity.id 
                            ? 'bg-blue-50 text-blue-600 font-medium' 
                            : 'hover:bg-slate-50 text-slate-700'
                        )}
                      >
                        {community.name}
                      </button>
                    ))}
                  </div>
                </>
              )}
            </div>

            {/* Admin User Menu */}
            <div className="flex items-center gap-4">
              <button className="relative p-2 text-slate-600 hover:bg-white rounded-lg transition-all duration-200 hover:shadow-sm group">
                <Bell className="w-5 h-5" />
                <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full">
                  <span className="absolute inset-0 bg-red-500 rounded-full pulse-ring"></span>
                </span>
              </button>

              <div className="relative">
                <button
                  onClick={() => setShowUserDropdown(!showUserDropdown)}
                  className="flex items-center gap-2 px-3 py-2 text-sm font-medium text-slate-900 hover:bg-white rounded-lg transition-all duration-200 hover:shadow-sm"
                >
                  <span>Admin User</span>
                  <ChevronDown className={cn(
                    "w-4 h-4 text-slate-500 transition-transform duration-200",
                    showUserDropdown && "rotate-180"
                  )} />
                </button>

                {showUserDropdown && (
                  <>
                    <div
                      className="fixed inset-0 z-10"
                      onClick={() => setShowUserDropdown(false)}
                    />
                    <div className="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-lg border border-slate-200 py-1 z-20 animate-in fade-in slide-in-from-top-2 duration-200">
                      <button className="w-full px-4 py-2.5 text-left text-sm text-slate-700 hover:bg-slate-50 transition-colors duration-150">
                        Profile Settings
                      </button>
                      <button className="w-full px-4 py-2.5 text-left text-sm text-slate-700 hover:bg-slate-50 transition-colors duration-150">
                        Preferences
                      </button>
                      <hr className="my-1 border-slate-200" />
                      <button className="w-full px-4 py-2.5 text-left text-sm text-red-600 hover:bg-red-50 transition-colors duration-150">
                        Sign Out
                      </button>
                    </div>
                  </>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* Navigation Tabs */}
        <div className="border-t border-slate-200">
          <nav className="flex px-6 gap-1">
            {navigation.map((item) => {
              const isActive = pathname === item.href || pathname.startsWith(item.href + '/');
              return (
                <Link
                  key={item.name}
                  href={item.href}
                  className={cn(
                    'px-4 py-3 text-sm font-medium border-b-2 transition-all duration-200',
                    isActive
                      ? 'border-blue-600 text-blue-600 bg-blue-50/30'
                      : 'border-transparent text-slate-600 hover:text-slate-900 hover:border-slate-300 hover:bg-slate-50/50'
                  )}
                >
                  {item.name}
                </Link>
              );
            })}
          </nav>
        </div>
      </header>

      {/* Main Content */}
      <main className="p-6">{children}</main>
    </div>
  );
}
