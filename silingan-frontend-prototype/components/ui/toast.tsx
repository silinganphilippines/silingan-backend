'use client';

import { Check } from 'lucide-react';
import { useEffect, useState } from 'react';
import { cn } from '@/lib/utils';

interface ToastProps {
  message: string;
  type?: 'success' | 'error' | 'info';
  duration?: number;
}

export function Toast({ message, type = 'success', duration = 3000 }: ToastProps) {
  const [visible, setVisible] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      setVisible(false);
    }, duration);

    return () => clearTimeout(timer);
  }, [duration]);

  if (!visible) return null;

  const types = {
    success: 'bg-green-600 text-white',
    error: 'bg-red-600 text-white',
    info: 'bg-blue-600 text-white'
  };

  return (
    <div className="fixed bottom-6 right-6 z-50 animate-slide-up">
      <div className={cn(
        "flex items-center gap-2 px-4 py-3 rounded-xl shadow-lg",
        types[type]
      )}>
        {type === 'success' && <Check className="w-5 h-5" />}
        <p className="font-medium">{message}</p>
      </div>
    </div>
  );
}
