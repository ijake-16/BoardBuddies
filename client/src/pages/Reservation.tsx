import { useState } from 'react';
import { Button } from '../components/Button';
import { ChevronLeftIcon } from 'lucide-react';
import { Calendar } from '../components/Calendar';

interface ReservationProps {
    onBack: () => void;
}


import { PageBackground } from '../components/PageBackground';

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
        <div className="flex-1 flex flex-col h-full overflow-hidden">
            <PageBackground />
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
                <div className="mt-8 flex justify-center">
                    <Button
                        disabled={selectedDays.length === 0}
                        className={`
                            w-80 h-12 bg-blue-950 rounded-[20px] text-white text-base font-semibold font-['Inter'] leading-4
                            transition-opacity duration-200
                            ${selectedDays.length === 0 ? 'opacity-50 cursor-not-allowed' : 'opacity-100 hover:bg-blue-900'}
                        `}
                    >
                        신청하기
                    </Button>
                </div>

            </main>
        </div>
    );
}
