import { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { eventApi, registrationApi } from '../services/api';
import { useAuth } from '../context/AuthContext';
import type { Event, EventStatus } from '../types';
import { 
  ArrowLeft, Clock, MapPin, Users, Award, 
  Download, CheckCircle, AlertCircle, Building, DollarSign 
} from 'lucide-react';
import toast from 'react-hot-toast';

const statusColors: Record<EventStatus, string> = {
  DRAFT: 'badge-draft', SUBMITTED: 'badge-submitted', UNDER_REVIEW: 'badge-submitted',
  APPROVED: 'badge-approved', REJECTED: 'badge-rejected', ONGOING: 'badge-ongoing',
  COMPLETED: 'badge-completed', ARCHIVED: 'badge-archived',
};

export default function EventDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { isStudent } = useAuth();
  const [event, setEvent] = useState<Event | null>(null);
  const [loading, setLoading] = useState(true);
  const [registering, setRegistering] = useState(false);
  const [timeLeft, setTimeLeft] = useState<{ days: number; hours: number; minutes: number; seconds: number; isPast: boolean }>({
    days: 0, hours: 0, minutes: 0, seconds: 0, isPast: false
  });

  const loadEvent = () => {
    if (!id) return;
    setLoading(true);
    eventApi.get(Number(id))
      .then((res) => {
        setEvent(res.data.data);
      })
      .catch(() => {
        toast.error('Failed to load event details');
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadEvent();
  }, [id]);

  // Dynamic live countdown timer
  useEffect(() => {
    if (!event || !event.startDate) return;

    const timer = setInterval(() => {
      const startDateTimeStr = event.startTime 
        ? `${event.startDate}T${event.startTime}`
        : `${event.startDate}T00:00:00`;
      
      const targetTime = new Date(startDateTimeStr).getTime();
      const now = new Date().getTime();
      const diff = targetTime - now;

      if (diff <= 0) {
        setTimeLeft({ days: 0, hours: 0, minutes: 0, seconds: 0, isPast: true });
      } else {
        const days = Math.floor(diff / (1000 * 60 * 60 * 24));
        const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((diff % (1000 * 60)) / 1000);
        setTimeLeft({ days, hours, minutes, seconds, isPast: false });
      }
    }, 1000);

    return () => clearInterval(timer);
  }, [event]);

  const handleRegister = async () => {
    if (!event) return;
    setRegistering(true);
    try {
      await registrationApi.register(event.id);
      toast.success('Successfully registered for this event!');
      loadEvent();
    } catch {
      toast.error('Registration failed or already registered');
    } finally {
      setRegistering(false);
    }
  };

  const handleDownloadCalendar = () => {
    if (!event) return;
    const downloadUrl = `/api/v1/events/${event.id}/calendar`;
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.setAttribute('download', `event-${event.id}.ics`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    toast.success('Calendar (.ics) invite downloaded');
  };

  if (loading) {
    return (
      <div className="card" style={{ textAlign: 'center', padding: '60px' }}>
        <p style={{ color: 'var(--text-secondary)' }}>Loading event details...</p>
      </div>
    );
  }

  if (!event) {
    return (
      <div className="card" style={{ textAlign: 'center', padding: '60px' }}>
        <h2>Event Not Found</h2>
        <Link to="/events" className="btn btn-primary" style={{ marginTop: 20 }}>
          Back to Events
        </Link>
      </div>
    );
  }

  const capacityPct = Math.min(100, Math.round(((event.registrationCount || 0) / (event.capacity || 1)) * 100));
  const seatsLeft = Math.max(0, (event.capacity || 0) - (event.registrationCount || 0));

  return (
    <div style={{ maxWidth: 1000, margin: '0 auto', display: 'flex', flexDirection: 'column', gap: 24 }}>
      {/* Back button and title bar */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 12 }}>
        <button onClick={() => navigate('/events')} className="btn btn-secondary btn-sm" style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
          <ArrowLeft size={16} /> Back to Events
        </button>
        <div style={{ display: 'flex', gap: 8 }}>
          <button onClick={handleDownloadCalendar} className="btn btn-secondary btn-sm" style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
            <Download size={16} /> Add to Calendar (.ics)
          </button>
          {isStudent && ['APPROVED', 'ONGOING'].includes(event.status) && (
            <button 
              onClick={handleRegister} 
              disabled={registering || seatsLeft === 0} 
              className="btn btn-primary btn-sm"
              style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}
            >
              <CheckCircle size={16} /> {seatsLeft === 0 ? 'Event Full' : registering ? 'Registering...' : 'Register Now'}
            </button>
          )}
        </div>
      </div>

      {/* Main Event Card */}
      <div className="card" style={{ padding: 28 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 12 }}>
          <div>
            <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>{event.eventCode}</span>
            <h1 style={{ fontSize: '1.8rem', margin: '6px 0 12px 0' }}>{event.title}</h1>
            <div style={{ display: 'flex', gap: 8, alignItems: 'center', flexWrap: 'wrap' }}>
              <span className={`badge ${statusColors[event.status]}`}>{event.status}</span>
              {event.categoryName && (
                <span className="badge" style={{ background: 'var(--bg-secondary)', color: 'var(--text-primary)' }}>
                  {event.categoryName}
                </span>
              )}
            </div>
          </div>
        </div>

        {/* Dynamic Live Countdown Box */}
        <div style={{
          marginTop: 24,
          padding: 16,
          borderRadius: 8,
          background: 'var(--bg-secondary)',
          border: '1px solid var(--border)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: 16
        }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8, fontWeight: 600 }}>
              <Clock size={18} style={{ color: 'var(--primary)' }} />
              <span>{timeLeft.isPast ? 'Event has commenced or concluded' : 'Countdown to Kickoff'}</span>
            </div>
            <small style={{ color: 'var(--text-secondary)' }}>
              Scheduled for {event.startDate} {event.startTime ? `@ ${event.startTime}` : ''}
            </small>
          </div>

          {!timeLeft.isPast && (
            <div style={{ display: 'flex', gap: 12 }}>
              <div style={{ textAlign: 'center', minWidth: 50, padding: '6px 10px', background: 'var(--bg-card)', borderRadius: 6, border: '1px solid var(--border)' }}>
                <span style={{ fontSize: '1.2rem', fontWeight: 700, display: 'block' }}>{timeLeft.days}</span>
                <span style={{ fontSize: '0.7rem', color: 'var(--text-secondary)', textTransform: 'uppercase' }}>Days</span>
              </div>
              <div style={{ textAlign: 'center', minWidth: 50, padding: '6px 10px', background: 'var(--bg-card)', borderRadius: 6, border: '1px solid var(--border)' }}>
                <span style={{ fontSize: '1.2rem', fontWeight: 700, display: 'block' }}>{timeLeft.hours}</span>
                <span style={{ fontSize: '0.7rem', color: 'var(--text-secondary)', textTransform: 'uppercase' }}>Hours</span>
              </div>
              <div style={{ textAlign: 'center', minWidth: 50, padding: '6px 10px', background: 'var(--bg-card)', borderRadius: 6, border: '1px solid var(--border)' }}>
                <span style={{ fontSize: '1.2rem', fontWeight: 700, display: 'block' }}>{timeLeft.minutes}</span>
                <span style={{ fontSize: '0.7rem', color: 'var(--text-secondary)', textTransform: 'uppercase' }}>Mins</span>
              </div>
              <div style={{ textAlign: 'center', minWidth: 50, padding: '6px 10px', background: 'var(--bg-card)', borderRadius: 6, border: '1px solid var(--border)' }}>
                <span style={{ fontSize: '1.2rem', fontWeight: 700, display: 'block', color: 'var(--primary)' }}>{timeLeft.seconds}</span>
                <span style={{ fontSize: '0.7rem', color: 'var(--text-secondary)', textTransform: 'uppercase' }}>Secs</span>
              </div>
            </div>
          )}
        </div>

        {/* Description */}
        <div style={{ marginTop: 24 }}>
          <h3 style={{ fontSize: '1.1rem', marginBottom: 8 }}>About This Event</h3>
          <p style={{ lineHeight: 1.6, color: 'var(--text-secondary)', whiteSpace: 'pre-wrap' }}>
            {event.description || 'No detailed description provided.'}
          </p>
        </div>

        {/* Capacity & Progress Bar */}
        <div style={{ marginTop: 24, padding: 16, borderRadius: 8, border: '1px solid var(--border)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8, fontSize: '0.9rem' }}>
            <span style={{ fontWeight: 600 }}>Seat Capacity & Registration</span>
            <span style={{ color: seatsLeft === 0 ? 'var(--danger)' : 'var(--text-secondary)' }}>
              {event.registrationCount} / {event.capacity} registered ({seatsLeft} seats left)
            </span>
          </div>
          <div style={{ width: '100%', height: 10, background: 'var(--bg-secondary)', borderRadius: 5, overflow: 'hidden' }}>
            <div style={{
              width: `${capacityPct}%`,
              height: '100%',
              background: capacityPct > 90 ? 'var(--danger)' : 'var(--primary)',
              transition: 'width 0.3s ease'
            }} />
          </div>
        </div>

        {/* Key Details Grid */}
        <div style={{
          marginTop: 24,
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
          gap: 16
        }}>
          <div style={{ padding: 14, borderRadius: 8, background: 'var(--bg-secondary)', display: 'flex', gap: 12, alignItems: 'center' }}>
            <MapPin size={22} style={{ color: 'var(--primary)' }} />
            <div>
              <small style={{ color: 'var(--text-secondary)' }}>Venue</small>
              <div style={{ fontWeight: 600 }}>{event.venueName || 'To Be Announced'}</div>
            </div>
          </div>

          <div style={{ padding: 14, borderRadius: 8, background: 'var(--bg-secondary)', display: 'flex', gap: 12, alignItems: 'center' }}>
            <Building size={22} style={{ color: 'var(--primary)' }} />
            <div>
              <small style={{ color: 'var(--text-secondary)' }}>Department</small>
              <div style={{ fontWeight: 600 }}>{event.departmentName || 'General Campus'}</div>
            </div>
          </div>

          <div style={{ padding: 14, borderRadius: 8, background: 'var(--bg-secondary)', display: 'flex', gap: 12, alignItems: 'center' }}>
            <Users size={22} style={{ color: 'var(--primary)' }} />
            <div>
              <small style={{ color: 'var(--text-secondary)' }}>Organizer</small>
              <div style={{ fontWeight: 600 }}>{event.organizerName || 'Faculty Committee'}</div>
            </div>
          </div>

          {event.approvedBudget != null && (
            <div style={{ padding: 14, borderRadius: 8, background: 'var(--bg-secondary)', display: 'flex', gap: 12, alignItems: 'center' }}>
              <DollarSign size={22} style={{ color: 'var(--primary)' }} />
              <div>
                <small style={{ color: 'var(--text-secondary)' }}>Approved Budget</small>
                <div style={{ fontWeight: 600 }}>₹{event.approvedBudget.toLocaleString()}</div>
              </div>
            </div>
          )}
        </div>

        {/* Objectives & Requirements */}
        {(event.objectives || event.requirements) && (
          <div style={{ marginTop: 24, display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: 16 }}>
            {event.objectives && (
              <div style={{ padding: 16, borderRadius: 8, border: '1px solid var(--border)' }}>
                <h4 style={{ fontSize: '0.95rem', marginBottom: 6, display: 'flex', alignItems: 'center', gap: 6 }}>
                  <Award size={16} /> Objectives
                </h4>
                <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', lineHeight: 1.5 }}>
                  {event.objectives}
                </p>
              </div>
            )}
            {event.requirements && (
              <div style={{ padding: 16, borderRadius: 8, border: '1px solid var(--border)' }}>
                <h4 style={{ fontSize: '0.95rem', marginBottom: 6, display: 'flex', alignItems: 'center', gap: 6 }}>
                  <AlertCircle size={16} /> Requirements
                </h4>
                <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', lineHeight: 1.5 }}>
                  {event.requirements}
                </p>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
