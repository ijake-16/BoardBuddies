import { useState } from 'react';
import { LowerMenuBar } from './components/LowerMenuBar';
import Home from './pages/Home';
import Reservation from './pages/Reservation';
import ReservationStats from './pages/ReservationStats';
import MyReservations from './pages/MyReservations';
import LoginLanding from './pages/LoginLanding';
import CrewDetail from './pages/CrewDetail';

function App() {
  const [activeTab, setActiveTab] = useState<'home' | 'calendar' | 'edit' | 'heart' | 'user'>('home');
  const [currentView, setCurrentView] = useState<'login' | 'home' | 'reservation' | 'stats' | 'my_reservations' | 'crew_detail'>('login');

  if (currentView === 'login') {
    return <LoginLanding onLogin={() => setCurrentView('home')} />;
  }

  return (
    <div className="min-h-screen bg-zinc-100 dark:bg-zinc-900 font-sans text-zinc-900 dark:text-zinc-100 flex justify-center">
      {/* Mobile Container */}
      <div className="w-full max-w-md bg-white dark:bg-zinc-950 min-h-screen relative shadow-2xl overflow-hidden flex flex-col">

        {currentView === 'reservation' ? (
          <Reservation onBack={() => setCurrentView('home')} />
        ) : currentView === 'stats' ? (
          <ReservationStats onBack={() => setCurrentView('home')} />
        ) : currentView === 'my_reservations' ? (
          <MyReservations onBack={() => setCurrentView('home')} />
        ) : currentView === 'crew_detail' ? (
          <CrewDetail
            onBack={() => setCurrentView('home')}
            onCalendarClick={() => setCurrentView('stats')}
          />
        ) : (
          <Home
            onReservationClick={() => setCurrentView('reservation')}
            onMyReservationsClick={() => setCurrentView('my_reservations')}
            onTeamClick={() => setCurrentView('crew_detail')}
          />
        )}

        {/* Fixed Bottom Menu */}
        <LowerMenuBar activeTab={activeTab} onTabChange={setActiveTab} className="absolute" />
      </div>
    </div>
  );
}

export default App;
