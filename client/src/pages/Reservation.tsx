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
        <div className="flex-1 flex flex-col h-full overflow-hidden bg-white">
            {/* Header */}
            <header className="px-6 pt-12 pb-4 flex items-center justify-between z-10">
                <Button variant="ghost" onClick={onBack} className="-ml-2 gap-1 text-zinc-500 hover:text-zinc-900">
                    <ChevronLeftIcon className="w-6 h-6" />
                </Button>
            </header>

            {/* Content */}
            <main className="flex-1 overflow-y-auto px-6 pb-[120px] flex flex-col items-center">

                {/* Page Title */}
                <div className="w-full mb-6">
                    <h1 className="text-3xl font-black italic text-zinc-900 font-['Joti_One']">December</h1>
                    <p className="text-sm font-medium text-zinc-900">2025</p>
                </div>

                {/* Grey Container */}
                <div className="w-full bg-[#F4F4F5] rounded-[30px] p-4 mb-8">
                    {/* Header Pill */}
                    <div className="w-full bg-white rounded-2xl py-3 shadow-sm mb-4 flex justify-center items-center">
                        <h2 className="text-base font-bold text-zinc-900">예약 달력</h2>
                    </div>

                    {/* Calendar Card */}
                    <div className="w-full bg-white rounded-[30px] p-6 shadow-sm">
                        <Calendar
                            month="December"
                            year={2025}
                            startDayOfWeek={1} // Dec 1 2025 is Monday
                            totalDays={31}
                            availableDays={availableDays}
                            selectedDays={selectedDays}
                            onDayClick={toggleDay}
                            hideHeader={true}
                        />
                    </div>
                </div>

                {/* Apply Button */}
                <div className="w-full flex justify-center mt-auto">
                    <Button
                        disabled={selectedDays.length === 0}
                        className={`
                            w-full h-14 bg-[#162660] rounded-[20px] text-white text-lg font-bold
                            transition-all duration-200 shadow-md
                            ${selectedDays.length === 0 ? 'opacity-50 cursor-not-allowed' : 'opacity-100 hover:bg-[#7A8C9F] hover:scale-[1.02]'}
                        `}
                    >
                        신청하기
                    </Button>
                </div>

            </main>
        </div>
    );
}
