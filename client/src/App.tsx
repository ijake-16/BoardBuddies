import { useState } from 'react';
import { LowerMenuBar } from './components/LowerMenuBar';
import Home from './pages/Home';
import Reservation from './pages/Reservation';
import ReservationStats from './pages/ReservationStats';
import MyReservations from './pages/MyReservations';
import LoginLanding from './pages/LoginLanding';
import CrewDetail from './pages/CrewDetail';
import UserInfoInput from './pages/UserInfoInput';
import SearchCrew from './pages/SearchCrew';

function App() {
  const [activeTab, setActiveTab] = useState<'home' | 'calendar' | 'edit' | 'heart' | 'user'>('home');
  const [currentView, setCurrentView] = useState<'login' | 'home' | 'reservation' | 'stats' | 'my_reservations' | 'crew_detail' | 'search_crew' | 'user_info'>('login');
  const [hasCrew, setHasCrew] = useState(false);

  return (
    <div className="w-full h-screen bg-zinc-100 dark:bg-zinc-900 flex items-center justify-center overflow-hidden">
      <div className="w-full h-full max-w-md bg-white dark:bg-black relative shadow-2xl overflow-hidden flex flex-col">
        {currentView === 'login' ? (
          <LoginLanding
            onLogin={() => setCurrentView('home')}
            onDebugUserInfo={() => setCurrentView('user_info')}
          />
        ) : currentView === 'reservation' ? (
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
        ) : currentView === 'search_crew' ? (
          <SearchCrew onBack={() => setCurrentView('home')} />
        ) : currentView === 'user_info' ? (
          <UserInfoInput onBack={() => setCurrentView('login')} />
        ) : (
          <Home
            onReservationClick={() => setCurrentView('reservation')}
            onTeamClick={() => setCurrentView('crew_detail')}
            onSearchClick={() => setCurrentView('search_crew')}
            hasCrew={hasCrew}
            onJoinCrew={() => setHasCrew(true)}
          />
        )}
      </div>
    </div>
  );
}

export default App;
