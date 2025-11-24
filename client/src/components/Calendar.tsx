

interface CalendarProps {
    month: string;
    year: number;
    startDayOfWeek: number; // 0 = Sunday, 1 = Monday, etc.
    totalDays: number;
    availableDays: number[];
    selectedDays: number[];
    onDayClick: (day: number) => void;
}

export const Calendar = ({
    month,
    year,
    startDayOfWeek,
    totalDays,
    availableDays,
    selectedDays,
    onDayClick,
}: CalendarProps) => {
    return (
        <div className="flex flex-col h-full">
            {/* Month Title */}
            <div className="mb-8">
                <h1 className="text-4xl mb-1" style={{ fontFamily: '"Joti One", serif' }}>{month}</h1>
                <p className="text-zinc-500 font-medium">{year}</p>
            </div>

            {/* Calendar Grid */}
            <div className="grid grid-cols-7 gap-y-8 gap-x-2 mb-auto">
                {/* Weekday Headers */}
                {['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'].map((day) => (
                    <div key={day} className="text-center text-zinc-400 text-sm font-medium">
                        {day}
                    </div>
                ))}

                {/* Empty cells for start of month */}
                {[...Array(startDayOfWeek)].map((_, i) => (
                    <div key={`empty-${i}`} />
                ))}

                {/* Days */}
                {[...Array(totalDays)].map((_, i) => {
                    const day = i + 1;
                    const isAvailable = availableDays.includes(day);
                    const isSelected = selectedDays.includes(day);

                    return (
                        <div key={day} className="flex items-center justify-center aspect-square">
                            <button
                                onClick={() => onDayClick(day)}
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
        </div>
    );
};
