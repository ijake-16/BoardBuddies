import { useState } from 'react';
import { ArrowLeftIcon, ChevronLeftIcon } from 'lucide-react';
import { Button } from '../components/Button';
import { Calendar } from '../components/Calendar';
import { cn } from '../lib/utils';

interface ReservationStatsProps {
    onBack: () => void;
}


import { PageBackground } from '../components/PageBackground';

export default function ReservationStats({ onBack }: ReservationStatsProps) {
    // Dummy data generation
    const getDayColor = (day: number) => {
        // Deterministic random-like based on day
        const val = (day * 7 + 3) % 20;

        if (val < 5) return 'bg-[#FF6B6B]'; // Red
        if (val < 10) return 'bg-[#F6C555]'; // Yellow
        return 'bg-[#A0D2EB]'; // Blue
    };

    return (
        <div className="flex-1 flex flex-col h-full overflow-hidden">
            <PageBackground />
            {/* Header */}
            <header className="px-6 pt-12 pb-4 flex items-center justify-between bg-white dark:bg-zinc-950 z-10 shadow-sm">
                <Button variant="ghost" onClick={onBack} className="-ml-2 gap-1 text-zinc-500 hover:text-zinc-900 dark:text-zinc-400 dark:hover:text-zinc-100">
                    <ChevronLeftIcon className="w-6 h-6" />
                    <span className="text-lg font-medium">Back</span>
                </Button>
            </header>

            <main className="flex-1 overflow-y-auto p-6 pb-[120px] flex flex-col">
                {/* Calendar Section */}
                <div className="flex-1">
                    <Calendar
                        month="December"
                        year={2025}
                        startDayOfWeek={1}
                        totalDays={31}
                        expandable={true}
                        renderDay={(day) => (
                            <div className={`w-10 h-10 rounded-full flex items-center justify-center text-sm font-bold text-zinc-800 shadow-sm ${getDayColor(day)}`}>
                                {day}
                            </div>
                        )}
                    />
                </div>

                {/* Legend Section */}
                <div className="w-full mt-8 flex flex-row justify-center items-center gap-8">
                    <h3 className="font-bold text-base mr-2">인원수</h3>

                    <div className="flex items-center gap-2">
                        <div className="w-4 h-4 rounded-full bg-[#FF6B6B] shadow-sm" />
                        <span className="text-xs font-medium text-zinc-500">~5</span>
                    </div>

                    <div className="flex items-center gap-2">
                        <div className="w-4 h-4 rounded-full bg-[#F6C555] shadow-sm" />
                        <span className="text-xs font-medium text-zinc-500">5~10</span>
                    </div>

                    <div className="flex items-center gap-2">
                        <div className="w-4 h-4 rounded-full bg-[#A0D2EB] shadow-sm" />
                        <span className="text-xs font-medium text-zinc-500">10~</span>
                    </div>
                </div>
            </main>
        </div>
    );
}
