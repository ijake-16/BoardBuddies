import { useState } from 'react';
import { Button } from './components/Button';

interface ReservationProps {
    onBack: () => void;
}

const ChevronLeftIcon = ({ className }: { className?: string }) => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
        <path d="m15 18-6-6 6-6" />
    </svg>
);

export default function Reservation({ onBack }: ReservationProps) {
    const [selectedDays, setSelectedDays] = useState<number[]>([]);

    // Mocking Today as Tuesday, Dec 14, 2025
    const today = 14;
    const availableStart = 15; // Wed
    const availableEnd = 18;   // Sat

    const toggleDay = (day: number) => {
        if (day < availableStart || day > availableEnd) return;

        setSelectedDays(prev =>
            prev.includes(day)
                ? prev.filter(d => d !== day)
                : [...prev, day]
        );
    };

    return (
        <div className="flex-1 flex flex-col bg-zinc-50 dark:bg-zinc-900 h-full overflow-hidden">
            {/* Header */}
            <header className="px-6 pt-12 pb-4 flex items-center justify-between bg-white dark:bg-zinc-950 z-10 shadow-sm">
                <Button variant="ghost" size="icon" onClick={onBack} className="-ml-2">
                    <ChevronLeftIcon className="w-8 h-8" />
                </Button>
                {/* Empty div for spacing balance if needed, or just align left/center */}
            </header>

            {/* Content */}
            <main className="flex-1 overflow-y-auto p-6 pb-[120px] flex flex-col">

                {/* Month Title */}
                <div className="mb-8">
                    <h1 className="text-4xl mb-1" style={{ fontFamily: '"Joti One", serif' }}>December</h1>
                    <p className="text-zinc-500 font-medium">2025</p>
                </div>

                {/* Calendar Grid */}
                <div className="grid grid-cols-7 gap-y-8 gap-x-2 mb-auto">
                    {/* Weekday Headers */}
                    {['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'].map((day) => (
                        <div key={day} className="text-center text-zinc-400 text-sm font-medium">
                            {day}
                        </div>
                    ))}

                    {/* Empty cells for start of month (Dec 1 2025 is Monday) */}
                    {/* 2025 Dec 1 is actually a Monday. So 1 empty cell (Sunday). */}
                    <div />

                    {/* Days 1-31 */}
                    {[...Array(31)].map((_, i) => {
                        const day = i + 1;
                        const isAvailable = day >= availableStart && day <= availableEnd;
                        const isSelected = selectedDays.includes(day);

                        return (
                            <div key={day} className="flex items-center justify-center aspect-square">
                                <button
                                    onClick={() => toggleDay(day)}
                                    disabled={!isAvailable}
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

                {/* Apply Button */}
                <div className="mt-8">
                    <Button
                        className="w-full bg-[#F6C555] hover:bg-[#e5b64e] text-black border-none h-14 text-lg font-bold rounded-2xl shadow-sm"
                    >
                        신청하기
                    </Button>
                </div>

            </main>
        </div>
    );
}
