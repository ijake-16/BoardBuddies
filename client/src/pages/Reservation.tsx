import { useState } from 'react';
import { Button } from './components/Button';
import { Calendar } from './components/Calendar';

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
    const availableStart = 15; // Wed
    const availableEnd = 18;   // Sat

    // Generate available days array
    const availableDays = Array.from(
        { length: availableEnd - availableStart + 1 },
        (_, i) => availableStart + i
    );

    const toggleDay = (day: number) => {
        if (!availableDays.includes(day)) return;

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
                <Button variant="ghost" onClick={onBack} className="-ml-2 gap-1 text-zinc-500 hover:text-zinc-900 dark:text-zinc-400 dark:hover:text-zinc-100">
                    <ChevronLeftIcon className="w-6 h-6" />
                    <span className="text-lg font-medium">Back</span>
                </Button>
                {/* Empty div for spacing balance if needed, or just align left/center */}
            </header>

            {/* Content */}
            <main className="flex-1 overflow-y-auto p-6 pb-[120px] flex flex-col">

                <Calendar
                    month="December"
                    year={2025}
                    startDayOfWeek={1} // Dec 1 2025 is Monday
                    totalDays={31}
                    availableDays={availableDays}
                    selectedDays={selectedDays}
                    onDayClick={toggleDay}
                />

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
