import { Button } from '../components/Button';
import { ChevronLeftIcon, ChevronRightIcon, Crown, UserPlusIcon } from 'lucide-react';
import { useEffect, useState } from 'react';
import { getCrewInfo } from '../services/crew';
import { getUserInfo } from '../services/user';
import { CrewDetail as CrewDetailType } from '../types/api';


interface CrewDetailProps {
    onBack: () => void;
    onCalendarClick: () => void;
    onMemberClick: () => void;
}

export default function CrewDetail({ onBack, onCalendarClick, onMemberClick }: CrewDetailProps) {
    const [crewInfo, setCrewInfo] = useState<CrewDetailType | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                // 1. Get User Info
                const userData = await getUserInfo();

                // 2. Extract Crew Info from User Data
                if (userData.crew) {
                    // 3. Fetch Full Crew Details
                    const crewData = await getCrewInfo(userData.crew.crewId);
                    setCrewInfo(crewData);
                } else {
                    console.warn('User does not belong to a crew.');
                }
            } catch (err: any) {
                console.error('Failed to fetch data:', err);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    if (loading) {
        return <div className="flex-1 flex items-center justify-center h-full bg-white dark:bg-zinc-950">Loading...</div>;
    }

    if (!crewInfo) {
        // Fallback if no crew info found (e.g. user not in crew)
        return (
            <div className="flex-1 flex flex-col items-center justify-center h-full bg-white dark:bg-zinc-950">
                <p className="text-zinc-500">크루 정보를 찾을 수 없습니다.</p>
                <Button variant="ghost" onClick={onBack} className="mt-4">
                    돌아가기
                </Button>
            </div>
        );
    }

    return (
        <div className="flex-1 flex flex-col h-full overflow-hidden bg-white dark:bg-zinc-950">
            {/* Header */}
            <header className="px-6 pt-12 pb-4 flex items-center justify-between relative">
                <Button variant="ghost" onClick={onBack} className="-ml-2 text-zinc-900 dark:text-zinc-100">
                    <ChevronLeftIcon className="w-6 h-6" />
                </Button>
                <h1 className="text-xl font-bold absolute left-1/2 -translate-x-1/2" style={{ fontFamily: '"Joti One", serif' }}>My Crew</h1>
                <div className="w-10" /> {/* Spacer for centering */}
            </header>

            <main className="flex-1 overflow-y-auto px-6 pt-4 pb-[120px] flex flex-col items-center">

                {/* Main Card */}
                <div className="w-full bg-[#FCD34D] rounded-[30px] p-6 shadow-sm mb-6">
                    {/* Inner White Card */}
                    <div className="bg-white rounded-[20px] p-5 flex items-center gap-4 mb-6">
                        {/* Placeholder Icon */}
                        {crewInfo.profile_image_url ? (
                            <img src={crewInfo.profile_image_url} alt={crewInfo.name} className="w-16 h-16 rounded-full object-cover shrink-0" />
                        ) : (
                            <div className="w-16 h-16 rounded-full bg-gradient-to-br from-pink-200 to-blue-200 flex items-center justify-center shrink-0">
                                {/* Fallback/Placeholder */}
                            </div>
                        )}

                        <div>
                            <h2 className="text-xl font-bold text-zinc-900">{crewInfo.name}</h2>
                            <p className="text-sm text-zinc-500 font-medium">{crewInfo.univ}</p>
                        </div>
                    </div>

                    {/* Stats Row */}
                    <div className="flex items-center justify-around px-2">
                        <div className="flex items-center gap-2">
                            <Crown className="w-5 h-5 text-zinc-800" />
                            <span className="text-sm font-medium text-zinc-800">{crewInfo.president_name}</span>
                        </div>
                        <button
                            onClick={onMemberClick}
                            className="flex items-center gap-2 hover:opacity-70 transition-opacity"
                        >
                            <UserPlusIcon className="w-5 h-5 text-zinc-800" />
                            <span className="text-sm font-medium text-zinc-800">부원수 : {crewInfo.member_count}명</span>
                        </button>
                    </div>
                </div>

                {/* Action Row */}
                <button
                    onClick={onCalendarClick}
                    className="w-full bg-zinc-100 rounded-[20px] p-5 flex items-center justify-between hover:bg-zinc-200 transition-colors mb-auto"
                >
                    <span className="font-bold text-zinc-600">크루 달력</span>
                    <div className="flex items-center gap-1 text-zinc-500">
                        <span className="text-sm font-medium">확인하기</span>
                        <ChevronRightIcon className="w-5 h-5" />
                    </div>
                </button>

                {/* Footer Text */}
                <div className="mb-8 text-center">
                    <p className="text-xs text-zinc-400 font-medium">
                        나의 크루에 가입하고 다양한 정보를 확인하세요!
                    </p>
                </div>

            </main >
        </div >
    );
}
