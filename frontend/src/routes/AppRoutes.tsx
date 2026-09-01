import { Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';
import LoginPage from '../pages/LoginPage';
import RegisterPage from '../pages/RegisterPage';
import DashboardPage from '../pages/DashboardPage';
import EventsPage from '../pages/EventsPage';
import PlaceholderPage from '../pages/PlaceholderPage';

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route element={<ProtectedRoute />}>
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/events" element={<EventsPage />} />
        <Route path="/events/:id" element={<PlaceholderPage title="Event Details" />} />
        <Route path="/events/new" element={<PlaceholderPage title="Create Event" />} />
        <Route path="/departments" element={<PlaceholderPage title="Departments" />} />
        <Route path="/bills" element={<PlaceholderPage title="Bills & Expenses" />} />
        <Route path="/attendance" element={<PlaceholderPage title="Attendance" />} />
        <Route path="/media" element={<PlaceholderPage title="Media Gallery" />} />
        <Route path="/reports" element={<PlaceholderPage title="Reports" />} />
        <Route path="/audit" element={<PlaceholderPage title="Audit Logs" />} />
        <Route path="/users" element={<PlaceholderPage title="User Management" />} />
        <Route path="/notifications" element={<PlaceholderPage title="Notifications" />} />
        <Route path="/settings" element={<PlaceholderPage title="Settings" />} />
      </Route>
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}
