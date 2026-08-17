import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import ChatPage from './pages/ChatPage';
import DashboardPage from './pages/DashboardPage';
import useAuthStore from './store/authStore';

const PrivateRoute = ({ children }) => {
  const { accessToken } = useAuthStore();
  return accessToken ? children : <Navigate to="/login" />;
};

function App() {
  return (
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/chat" element={<PrivateRoute><ChatPage /></PrivateRoute>} />
          <Route path="/dashboard/:roomId" element={<PrivateRoute><DashboardPage /></PrivateRoute>} />
          <Route path="*" element={<Navigate to="/login" />} />
        </Routes>
      </BrowserRouter>
  );
}

export default App;