import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { eventApi, registrationApi } from '../services/api';
import { useAuth } from '../context/AuthContext';
import type { Event, EventStatus } from '../types';
import { Plus, Search, Filter } from 'lucide-react';
import toast from 'react-hot-toast';
import './EventsPage.css';

const statusColors: Record<EventStatus, string> = {
  DRAFT: 'badge-draft', SUBMITTED: 'badge-submitted', UNDER_REVIEW: 'badge-submitted',
  APPROVED: 'badge-approved', REJECTED: 'badge-rejected', ONGOING: 'badge-ongoing',
  COMPLETED: 'badge-completed', ARCHIVED: 'badge-archived',
};

export default function EventsPage() {
  const { isAdmin, isOperator, isStudent } = useAuth();
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('');

  const loadEvents = () => {
    setLoading(true);
    const params: Record<string, unknown> = { size: 50 };
    if (statusFilter) params.status = statusFilter;
    if (search) params.search = search;
    eventApi.list(params)
      .then((res) => setEvents(res.data.data.content))
      .catch(() => toast.error('Failed to load events'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { loadEvents(); }, [statusFilter]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    loadEvents();
  };

  const handleApprove = async (id: number) => {
    try {
      await eventApi.approve(id, 'Approved by admin');
      toast.success('Event approved');
      loadEvents();
    } catch { toast.error('Failed to approve'); }
  };

  const handleRegister = async (id: number) => {
    try {
      await registrationApi.register(id);
      toast.success('Registered successfully!');
      loadEvents();
    } catch { toast.error('Registration failed'); }
  };

  return (
    <div className="events-page">
      <div className="page-header">
        <h1 className="page-title">Events</h1>
        {(isAdmin || isOperator) && (
          <Link to="/events/new" className="btn btn-primary">
            <Plus size={18} /> New Event
          </Link>
        )}
      </div>

      <div className="filters-bar">
        <form onSubmit={handleSearch} className="search-form">
          <Search size={18} />
          <input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search events..."
          />
        </form>
        <div className="filter-select">
          <Filter size={16} />
          <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="">All Status</option>
            <option value="DRAFT">Draft</option>
            <option value="SUBMITTED">Submitted</option>
            <option value="APPROVED">Approved</option>
            <option value="ONGOING">Ongoing</option>
            <option value="COMPLETED">Completed</option>
          </select>
        </div>
      </div>

      {loading ? (
        <div className="loading">Loading events...</div>
      ) : (
        <div className="events-grid">
          {events.map((event) => (
            <div key={event.id} className="event-card card">
              <div className="event-card-header">
                <span className="event-code">{event.eventCode}</span>
                <span className={`badge ${statusColors[event.status]}`}>{event.status}</span>
              </div>
              <h3>{event.title}</h3>
              <p className="event-desc">{event.description?.substring(0, 120)}...</p>
              <div className="event-meta">
                <span>{event.departmentName || 'N/A'}</span>
                <span>{event.startDate}</span>
                <span>{event.registrationCount}/{event.capacity} registered</span>
              </div>
              {event.approvedBudget != null && event.approvedBudget > 0 && (
                <div className="budget-bar">
                  <div className="budget-fill" style={{
                    width: `${Math.min(event.budgetUtilization || 0, 100)}%`,
                    background: (event.budgetUtilization || 0) > 90 ? 'var(--danger)' : 'var(--accent)'
                  }} />
                  <span className="budget-label">
                    Budget: ₹{event.actualSpending?.toLocaleString()} / ₹{event.approvedBudget?.toLocaleString()}
                  </span>
                </div>
              )}
              <div className="event-actions">
                <Link to={`/events/${event.id}`} className="btn btn-secondary btn-sm">View</Link>
                {isAdmin && event.status === 'SUBMITTED' && (
                  <button className="btn btn-primary btn-sm" onClick={() => handleApprove(event.id)}>Approve</button>
                )}
                {isStudent && ['APPROVED', 'ONGOING'].includes(event.status) && (
                  <button className="btn btn-primary btn-sm" onClick={() => handleRegister(event.id)}>Register</button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
