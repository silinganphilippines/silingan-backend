import * as React from "react";
import { cn } from "@/lib/utils";

export interface BadgeProps extends React.HTMLAttributes<HTMLDivElement> {
  variant?: 'default' | 'success' | 'warning' | 'danger' | 'info' | 'purple' | 'pink';
  dot?: boolean;
}

const Badge = React.forwardRef<HTMLDivElement, BadgeProps>(
  ({ className, variant = 'default', dot = false, children, ...props }, ref) => {
    const variants = {
      default: 'bg-slate-100 text-slate-800 border-slate-200',
      success: 'bg-emerald-100 text-emerald-800 border-emerald-200',
      warning: 'bg-amber-100 text-amber-800 border-amber-200',
      danger: 'bg-red-100 text-red-800 border-red-200',
      info: 'bg-blue-100 text-blue-800 border-blue-200',
      purple: 'bg-purple-100 text-purple-800 border-purple-200',
      pink: 'bg-pink-100 text-pink-800 border-pink-200'
    };

    const dotColors = {
      default: 'bg-slate-600',
      success: 'bg-emerald-600',
      warning: 'bg-amber-600',
      danger: 'bg-red-600',
      info: 'bg-blue-600',
      purple: 'bg-purple-600',
      pink: 'bg-pink-600'
    };

    return (
      <div
        ref={ref}
        className={cn(
          "inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold border transition-colors duration-150",
          variants[variant],
          className
        )}
        {...props}
      >
        {dot && (
          <span className={cn("w-1.5 h-1.5 rounded-full mr-1.5", dotColors[variant])} />
        )}
        {children}
      </div>
    );
  }
);
Badge.displayName = "Badge";

export { Badge };
