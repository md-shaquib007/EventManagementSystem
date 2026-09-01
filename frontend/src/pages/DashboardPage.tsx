import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { dashboardApi } from '../services/api';
import type { DashboardData } from '../types';
import StatCard from '../components/StatCard';
import {
  Calendar, Users, IndianRupee, FileText, CheckCircle,
  Building2, Bell, Award, ClipboardList
} from 'lucide-react';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, LineChart, Line
} from 'recharts';
import './DashboardPage.css';

const COLORS = ['#6366f1', '#8b5cf6', '#a78bfa', '#c4b5fd', '#10b981', '#f59e0b'];

export default function DashboardPage() {
  const { isAdmin, isOperator, isStudent } = useAuth();
  const [data, setData] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetch = isAdmin ? dashboardApi.admin
      : isOperator ? dashboardApi.operator
      : dashboardApi.student;
    fetch()
      .then((res) => setData(res.data.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [isAdmin, isOperator]);

  if (loading) return <div className="loading">Loading dashboard...</div>;
  if (!data) return <div className="loading">Failed to load dashboard</div>;

  const formatCurrency = (val?: number) =>
    val != null ? `₹${Number(val).toLocaleString('en-IN')}` : '₹0';

  return (
    <div className="dashboard">
      <h1 className="page-title">
        {isAdmin ? 'Admin Dashboard' : isOperator ? 'Operator Dashboard' : 'Student Dashboard'}
      </h1>

      <div className="grid grid-4 stats-grid">
        {isAdmin && (
          <>
            <StatCard title="Total Events" value={data.totalEvents ?? 0} icon={Calendar} color="#6366f1" />
            <StatCard title="Active Events" value={data.activeEvents ?? 0} icon={CheckCircle} color="#10b981" />
            <StatCard title="Upcoming" value={data.upcomingEvents ?? 0} icon={Calendar} color="#3b82f6" />
            <StatCard title="Completed" value={data.completedEvents ?? 0} icon={Award} color="#8b5cf6" />
            <StatCard title="Operators" value={data.operators ?? 0} icon={Users} color="#f59e0b" />
            <StatCard title="Departments" value={data.departments ?? 0} icon={Building2} color="#ec4899" />
            <StatCard title="Total Budget" value={formatCurrency(data.totalBudget)} icon={IndianRupee} color="#10b981" />
            <StatCard title="Total Expenses" value={formatCurrency(data.totalExpenses)} icon={IndianRupee} color="#ef4444" />
            <StatCard title="Pending Bills" value={data.pendingBills ?? 0} icon={FileText} color="#f59e0b" />
            <StatCard title="Pending Approvals" value={data.pendingApprovals ?? 0} icon={ClipboardList} color="#6366f1" />
          </>
        )}
        {isOperator && !isAdmin && (
          <>
            <StatCard title="Assigned Events" value={data.assignedEvents ?? 0} icon={Calendar} />
            <StatCard title="Today's Events" value={data.todaysEvents ?? 0} icon={Calendar} color="#10b981" />
            <StatCard title="Pending Tasks" value={data.pendingTasks ?? 0} icon={ClipboardList} color="#f59e0b" />
            <StatCard title="Registrations" value={data.registrations ?? 0} icon={Users} color="#3b82f6" />
            <StatCard title="Expenses" value={formatCurrency(data.totalExpenses)} icon={IndianRupee} color="#ef4444" />
            <StatCard title="Uploaded Bills" value={data.uploadedBills ?? 0} icon={FileText} />
          </>
        )}
        {isStudent && (
          <>
            <StatCard title="Registered Events" value={data.registeredEvents ?? 0} icon={Calendar} />
            <StatCard title="Upcoming Events" value={data.upcomingEvents ?? 0} icon={Calendar} color="#10b981" />
            <StatCard title="Certificates" value={data.certificates ?? 0} icon={Award} color="#8b5cf6" />
            <StatCard title="Notifications" value={data.notifications ?? 0} icon={Bell} color="#f59e0b" />
          </>
        )}
      </div>

      {(isAdmin || isOperator) && data.monthlyEvents && (
        <div className="grid grid-2 charts-grid">
          <div className="card chart-card">
            <h3>Monthly Events</h3>
            <ResponsiveContainer width="100%" height={280}>
              <BarChart data={data.monthlyEvents}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" />
                <XAxis dataKey="month" stroke="var(--text-muted)" />
                <YAxis stroke="var(--text-muted)" />
                <Tooltip contentStyle={{ background: 'var(--bg-card)', border: '1px solid var(--border)' }} />
                <Bar dataKey="count" fill="#6366f1" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>

          {data.expenseTrends && data.expenseTrends.length > 0 && (
            <div className="card chart-card">
              <h3>Expense by Category</h3>
              <ResponsiveContainer width="100%" height={280}>
                <PieChart>
                  <Pie data={data.expenseTrends} dataKey="amount" nameKey="category"
                       cx="50%" cy="50%" outerRadius={100} label>
                    {data.expenseTrends.map((_, i) => (
                      <Cell key={i} fill={COLORS[i % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip contentStyle={{ background: 'var(--bg-card)', border: '1px solid var(--border)' }} />
                </PieChart>
              </ResponsiveContainer>
            </div>
          )}

          {data.attendanceAnalysis && (
            <div className="card chart-card">
              <h3>Attendance Trends</h3>
              <ResponsiveContainer width="100%" height={280}>
                <LineChart data={data.attendanceAnalysis}>
                  <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" />
                  <XAxis dataKey="month" stroke="var(--text-muted)" />
                  <YAxis stroke="var(--text-muted)" />
                  <Tooltip contentStyle={{ background: 'var(--bg-card)', border: '1px solid var(--border)' }} />
                  <Line type="monotone" dataKey="count" stroke="#10b981" strokeWidth={2} />
                </LineChart>
              </ResponsiveContainer>
            </div>
          )}

          {data.departmentPerformance && (
            <div className="card chart-card">
              <h3>Department Performance</h3>
              <ResponsiveContainer width="100%" height={280}>
                <BarChart data={data.departmentPerformance} layout="vertical">
                  <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" />
                  <XAxis type="number" stroke="var(--text-muted)" />
                  <YAxis dataKey="department" type="category" width={120} stroke="var(--text-muted)" />
                  <Tooltip contentStyle={{ background: 'var(--bg-card)', border: '1px solid var(--border)' }} />
                  <Bar dataKey="events" fill="#8b5cf6" radius={[0, 6, 6, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
