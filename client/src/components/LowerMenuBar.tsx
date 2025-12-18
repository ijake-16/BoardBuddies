import React from 'react';
import { cn } from '../lib/utils';
import { Button } from './Button'; // Import Button

interface IconProps extends React.SVGProps<SVGSVGElement> { }

const HomeIcon = ({ className, ...props }: IconProps) => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    width="35"
    height="35"
    viewBox="0 0 35 35"
    fill="none"
    className={className}
    {...props}
  >
    <path
      fillRule="evenodd"
      clipRule="evenodd"
      d="M17.5 9.72558L29.163 20.0922L29.1636 26.2344C29.1636 28.6042 27.2897 30.5354 24.9455 30.622L24.7812 30.625H21.857C21.0869 30.625 20.456 30.028 20.4002 29.2707L20.3962 29.1615V25.7745C20.3962 24.2125 19.1683 22.9373 17.6251 22.8614L17.4795 22.8578C15.9127 22.8578 14.6373 24.0857 14.5615 25.6289L14.5579 25.7745V29.1615C14.5579 29.933 13.962 30.5651 13.2061 30.621L13.0971 30.625H10.173C7.8076 30.625 5.87992 28.7476 5.79352 26.399L5.7903 26.2344V20.1338L17.5 9.72558ZM16.5129 4.73903C17.0319 4.28221 17.7936 4.25534 18.3412 4.65841L18.4412 4.73903L31.5886 16.4472C32.1929 16.981 32.2518 17.9043 31.7201 18.5107C31.2671 19.0254 30.5333 19.1452 29.9516 18.8423L18.4688 8.63557L18.3685 8.554C17.8536 8.17165 17.1463 8.17166 16.6314 8.55403L16.5311 8.63561L5.18186 18.7244C4.58012 19.123 3.7666 19.0225 3.28083 18.4695C2.79139 17.9123 2.79914 17.0829 3.28087 16.5357L3.36554 16.4472L16.5129 4.73903Z"
      fill="currentColor"
    />
  </svg>
);

// Placeholder Icons
const CalendarIcon = ({ className, ...props }: IconProps) => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    width="35"
    height="35"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
    className={className}
    {...props}
  >
    <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
    <line x1="16" y1="2" x2="16" y2="6" />
    <line x1="8" y1="2" x2="8" y2="6" />
    <line x1="3" y1="10" x2="21" y2="10" />
  </svg>
);

const EditIcon = ({ className, ...props }: IconProps) => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    width="35"
    height="35"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
    className={className}
    {...props}
  >
    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
  </svg>
);

const HeartIcon = ({ className, ...props }: IconProps) => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    width="35"
    height="35"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
    className={className}
    {...props}
  >
    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
  </svg>
);

const UserIcon = ({ className, ...props }: IconProps) => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    width="35"
    height="35"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
    className={className}
    {...props}
  >
    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
    <circle cx="12" cy="7" r="4" />
  </svg>
);

interface LowerMenuBarProps {
  className?: string;
  activeTab?: 'home' | 'calendar' | 'edit' | 'heart' | 'user';
  onTabChange?: (tab: 'home' | 'calendar' | 'edit' | 'heart' | 'user') => void;
}

export const LowerMenuBar = ({
  className,
  activeTab = 'home',
  onTabChange,
}: LowerMenuBarProps) => {
  const menuItems = [
    { id: 'home', icon: HomeIcon, label: 'Home' },
    { id: 'calendar', icon: CalendarIcon, label: 'Reservation Calendar' },
    { id: 'edit', icon: EditIcon, label: 'Make Reservation' },
    { id: 'heart', icon: HeartIcon, label: 'My Crew' },
    { id: 'user', icon: UserIcon, label: 'Profile' },
  ] as const;

  return (
    <nav
      className={cn(
        'absolute bottom-2 left-1/2 -translate-x-1/2 z-50',
        'flex items-center justify-around',
        'bg-white dark:bg-zinc-900',
        'shadow-[0_4px_20px_rgba(0,0,0,0.1)]',
        'py-2 px-6 h-auto w-[75%] max-w-[380px]',
        'rounded-[24px]',
        className
      )}
    >
      {menuItems.map((item) => {
        const isActive = activeTab === item.id;
        const Icon = item.icon;

        return (
          <Button
            key={item.id}
            variant="ghost"
            size="icon" // Use the new size
            onClick={() => {
              if (item.id === 'user') return; // Disable Profile Link
              onTabChange?.(item.id);
            }}
            className={cn(
              'flex flex-col items-center justify-center transition-colors duration-200 h-auto w-auto p-2',
              isActive
                ? 'text-black dark:text-white'
                : 'text-zinc-400 hover:text-zinc-600 dark:text-zinc-600 dark:hover:text-zinc-400'
            )}
            aria-label={item.label}
          >
            <Icon className="w-8 h-8 md:w-9 md:h-9" />
          </Button>
        );
      })}
    </nav>
  );
};

export default LowerMenuBar;

