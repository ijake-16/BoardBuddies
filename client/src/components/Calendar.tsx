import { useState, useMemo } from 'react';
import { Button } from './Button';

interface CalendarProps {
    month: string;
    year: number;
    startDayOfWeek: number; // 0 = Sunday, 1 = Monday, etc.
    totalDays: number;
    availableDays?: number[];
    selectedDays?: number[];
    onDayClick?: (day: number) => void;
    renderDay?: (day: number) => React.ReactNode;
    expandable?: boolean;
    hideHeader?: boolean;
    maxWeeks?: number;
}

export const Calendar = ({
    month,
    year,
    startDayOfWeek,
    totalDays,
    availableDays = [],
    selectedDays = [],
    onDayClick,
    renderDay,
    expandable = false,
    hideHeader = false,
    maxWeeks,
}: CalendarProps) => {
    const [viewMode, setViewMode] = useState<'month' | 'week'>('month');
    const [focusedWeekIndex, setFocusedWeekIndex] = useState<number | null>(null);

    // Group days into weeks
    const weeks = useMemo(() => {
        const weeksArray: (number | null)[][] = [];
        let currentWeek: (number | null)[] = [];

        // Add empty slots for start of month
        for (let i = 0; i < startDayOfWeek; i++) {
            currentWeek.push(null);
        }

        // Add days
        for (let day = 1; day <= totalDays; day++) {
            currentWeek.push(day);
            if (currentWeek.length === 7) {
                weeksArray.push(currentWeek);
                currentWeek = [];
            }
        }

        // Fill remaining slots in last week
        if (currentWeek.length > 0) {
            while (currentWeek.length < 7) {
                currentWeek.push(null);
            }
            weeksArray.push(currentWeek);
        }

        if (maxWeeks) {
            return weeksArray.slice(0, maxWeeks);
        }

        return weeksArray;
    }, [startDayOfWeek, totalDays, maxWeeks]);

    const handleDayClick = (day: number, weekIndex: number) => {
        if (expandable && viewMode === 'month') {
            setFocusedWeekIndex(weekIndex);
            setViewMode('week');
        } else {
            onDayClick?.(day);
        }
    };

    const handleBackToMonth = () => {
        setViewMode('month');
    };

    return (
        <div className="flex flex-col h-full">
            {/* Month Title */}
            {!hideHeader && (
                <div className="mb-6 flex items-center justify-center relative">
                    <div className="text-center">
                        <h1 className="text-lg font-bold text-zinc-900">{month}</h1>
                        <p className="text-xs text-zinc-400 font-medium">{year}</p>
                    </div>
                </div>
            )}

            {/* Calendar Grid Header */}
            <div className="grid grid-cols-7 gap-x-2 mb-6">
                {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map((day) => (
                    <div key={day} className="text-center text-zinc-400 text-sm font-bold">
                        {day}
                    </div>
                ))}
            </div>

            {/* Weeks Rows */}
            <div className="flex flex-col">
                {weeks.map((week, weekIndex) => {
                    // Determine if this row should be visible
                    // In month view: All visible
                    // In week view: Only the focused week is visible
                    const isFocused = focusedWeekIndex === weekIndex;
                    const isHidden = viewMode === 'week' && !isFocused;

                    return (
                        <div
                            key={weekIndex}
                            className={`
                                grid grid-cols-7 gap-x-2 transition-all duration-500 ease-in-out overflow-hidden
                                ${isHidden ? 'max-h-0 opacity-0 mb-0' : 'max-h-[60px] opacity-100 mb-8'}
                            `}
                        >
                            {week.map((day, dayIndex) => {
                                if (day === null) {
                                    return <div key={`empty-${weekIndex}-${dayIndex}`} />;
                                }

                                const isAvailable = availableDays.includes(day);
                                const isSelected = selectedDays.includes(day);

                                if (renderDay) {
                                    return (
                                        <div key={day} className="flex items-center justify-center aspect-square" onClick={() => handleDayClick(day, weekIndex)}>
                                            {renderDay(day)}
                                        </div>
                                    )
                                }

                                return (
                                    <div key={day} className="flex items-center justify-center aspect-square">
                                        <button
                                            onClick={() => handleDayClick(day, weekIndex)}
                                            disabled={!isAvailable && !expandable}
                                            className={`
                                                w-10 h-10 rounded-full flex items-center justify-center text-sm font-medium transition-all
                                                ${isSelected ? 'bg-[#F6C555] text-black shadow-sm' : ''}
                                                ${!isSelected && isAvailable ? 'text-black dark:text-white hover:bg-zinc-100 dark:hover:bg-zinc-800' : ''}
                                                ${!isSelected && !isAvailable ? 'text-zinc-300 dark:text-zinc-700 cursor-default' : ''}
                                            `}
                                        >
                                            {day}
                                        </button>
                                    </div>
                                );
                            })}
                        </div>
                    );
                })}
            </div>

            {/* Back to Month Button (Bottom) */}
            <div className={`mt-auto flex justify-center transition-opacity duration-300 ${viewMode === 'week' ? 'opacity-100' : 'opacity-0 pointer-events-none h-0'}`}>
                <Button variant="ghost" size="small" onClick={handleBackToMonth} className="text-zinc-400 hover:text-zinc-600 text-xs">
                    Back to Month
                </Button>
            </div>
        </div>
    );
};
