import { Button } from '../components/Button';
import { ChevronLeftIcon, SaveIcon } from 'lucide-react';
import { useEffect, useState } from 'react';
import { getCrewInfo, getCrewManagers, updateCrew } from '../services/crew';
import { getUserInfo } from '../services/user';
import { CrewUpdateRequest } from '../types/api';
import Promote from './Promote';

interface CrewSettingsProps {
    onBack: () => void;
}

export default function CrewSettings({ onBack }: CrewSettingsProps) {
    const [loading, setLoading] = useState(true);
    const [crewId, setCrewId] = useState<number | null>(null);
    const [showPromote, setShowPromote] = useState(false);
    const [formData, setFormData] = useState<CrewUpdateRequest>({
        crewName: '',
        manager_list: [],
        crewPIN: 0,
        reservation_day: 'FRIDAY',
        reservation_time: '18:00',
        dailyCapacity: 0,
        isCapacityLimited: false
    });

    useEffect(() => {
        const fetchData = async () => {
            try {
                const user = await getUserInfo();
                if (user.crew) {
                    const cId = user.crew.crewId;
                    setCrewId(cId);
                    const [info, managers] = await Promise.all([
                        getCrewInfo(cId),
                        getCrewManagers(cId)
                    ]);

                    setFormData({
                        crewName: info.name,
                        manager_list: [], // Force empty list
                        crewPIN: 1234, // Default to number
                        reservation_day: info.reservation_day,
                        reservation_time: info.reservation_time,
                        dailyCapacity: info.dailyCapacity,
                        isCapacityLimited: info.isCapacityLimited
                    });
                }
            } catch (error) {
                console.error("Failed to fetch crew settings", error);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, []);

    const handleChange = (field: keyof CrewUpdateRequest, value: any) => {
        setFormData(prev => ({ ...prev, [field]: value }));
    };

    const handleSave = async () => {
        if (!crewId) return;

        // Construct payload with possible aliases to handle API strictness
        const payload = {
            ...formData,
            id: crewId,
            name: formData.crewName,
            manager_list: [], // FORCE in payload construction too, just in case
        };

        // Debug: Show payload
        const confirmed = window.confirm(`전송할 데이터 (Forced Empty Manager List):\n${JSON.stringify(payload, null, 2)}\n\n저장하시겠습니까?`);
        if (!confirmed) return;

        try {
            console.log("Saving Crew Settings:", { crewId, payload });
            await updateCrew(crewId, payload as any);
            alert("설정이 저장되었습니다.");
            onBack();
        } catch (error) {
            console.error("Failed to update crew", error);
            alert("저장 중 오류가 발생했습니다. (Console을 확인해주세요)");
        }
    };

    if (loading) return <div className="flex-1 flex items-center justify-center">로딩 중...</div>;

    if (showPromote && crewId) {
        return <Promote crewId={crewId} onBack={() => setShowPromote(false)} />;
    }

    return (
        <div className="flex-1 flex flex-col h-full overflow-hidden bg-white dark:bg-zinc-950">
            {/* Header */}
            <header className="px-6 pt-12 pb-4 flex items-center justify-between relative bg-white dark:bg-zinc-950 z-10">
                <Button variant="ghost" onClick={onBack} className="-ml-2 text-zinc-900 dark:text-zinc-100">
                    <ChevronLeftIcon className="w-6 h-6" />
                </Button>
                <h1 className="flex-1 text-center text-lg font-bold text-zinc-900">크루 설정</h1>
                <div className="w-10" />
            </header>

            <main className="flex-1 overflow-y-auto px-6 py-6 pb-28">
                <div className="space-y-6">
                    {/* Crew Name */}
                    <div className="space-y-2">
                        <label className="text-sm font-bold text-zinc-900">크루 이름</label>
                        <input
                            type="text"
                            value={formData.crewName}
                            onChange={(e) => handleChange('crewName', e.target.value)}
                            className="w-full px-4 py-3 rounded-xl bg-zinc-50 border-none focus:ring-2 focus:ring-black/5"
                        />
                    </div>

                    {/* Crew PIN */}
                    <div className="space-y-2">
                        <label className="text-sm font-bold text-zinc-900">가입 PIN</label>
                        <input
                            type="number"
                            value={formData.crewPIN}
                            onChange={(e) => handleChange('crewPIN', parseInt(e.target.value) || 0)}
                            className="w-full px-4 py-3 rounded-xl bg-zinc-50 border-none focus:ring-2 focus:ring-black/5"
                        />
                    </div>

                    {/* Reservation Day */}
                    <div className="space-y-2">
                        <label className="text-sm font-bold text-zinc-900">다음 주 예약 개시 요일</label>
                        <select
                            value={formData.reservation_day}
                            onChange={(e) => handleChange('reservation_day', e.target.value)}
                            className="w-full px-4 py-3 rounded-xl bg-zinc-50 border-none focus:ring-2 focus:ring-black/5"
                        >
                            {['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'].map(day => (
                                <option key={day} value={day}>{day}</option>
                            ))}
                        </select>
                    </div>

                    {/* Reservation Time */}
                    <div className="space-y-2">
                        <label className="text-sm font-bold text-zinc-900">다음 주 예약 개시 시간</label>
                        <input
                            type="time"
                            value={formData.reservation_time}
                            onChange={(e) => handleChange('reservation_time', e.target.value)}
                            className="w-full px-4 py-3 rounded-xl bg-zinc-50 border-none focus:ring-2 focus:ring-black/5"
                        />
                    </div>

                    {/* Daily Capacity */}
                    <div className="space-y-2">
                        <div className="flex items-center justify-between">
                            <label className="text-sm font-bold text-zinc-900">일일 정원</label>
                            <label className="flex items-center gap-2 text-sm text-zinc-500">
                                <input
                                    type="checkbox"
                                    checked={formData.isCapacityLimited}
                                    onChange={(e) => handleChange('isCapacityLimited', e.target.checked)}
                                    className="rounded border-zinc-300 text-zinc-900 focus:ring-zinc-900"
                                />
                                정원 제한
                            </label>
                        </div>
                        <input
                            type="number"
                            value={formData.dailyCapacity}
                            onChange={(e) => handleChange('dailyCapacity', parseInt(e.target.value) || 0)}
                            disabled={!formData.isCapacityLimited}
                            className={`w-full px-4 py-3 rounded-xl bg-zinc-50 border-none focus:ring-2 focus:ring-black/5 ${!formData.isCapacityLimited ? 'opacity-50' : ''
                                }`}
                        />
                    </div>

                    {/* Save Button */}
                    <button
                        onClick={handleSave}
                        className="w-full py-4 bg-zinc-900 text-white rounded-2xl font-bold text-lg mt-8 hover:bg-zinc-800 transition-colors flex items-center justify-center gap-2"
                    >
                        <SaveIcon className="w-5 h-5" />
                        저장하기
                    </button>


                    {/* Manage Managers Button */}
                    <button
                        onClick={() => setShowPromote(true)}
                        className="w-full py-4 bg-white border-2 border-zinc-100 text-zinc-900 rounded-2xl font-bold text-lg hover:bg-zinc-50 transition-colors"
                    >
                        운영진 관리
                    </button>

                    <div className="h-10" />
                </div>
            </main>
        </div>
    );
}
