import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { eventApi, masterApi } from '../services/api';
import type { Department, EventCategory, Venue } from '../types';
import { ArrowLeft } from 'lucide-react';
import toast from 'react-hot-toast';

export default function CreateEventPage() {
  const navigate = useNavigate();
  const [submitting, setSubmitting] = useState(false);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [categories, setCategories] = useState<EventCategory[]>([]);
  const [venues, setVenues] = useState<Venue[]>([]);

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    categoryId: '',
    departmentId: '',
    venueId: '',
    startDate: '',
    endDate: '',
    startTime: '09:00',
    endTime: '17:00',
    capacity: 100,
    estimatedBudget: 5000,
    objectives: '',
    requirements: '',
    facultyCoordinator: '',
    studentCoordinator: '',
  });

  useEffect(() => {
    masterApi.departments().then((res) => setDepartments(res.data.data)).catch(() => {});
    masterApi.categories().then((res) => setCategories(res.data.data)).catch(() => {});
    masterApi.venues().then((res) => setVenues(res.data.data)).catch(() => {});
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.title.trim()) {
      toast.error('Event title is required');
      return;
    }
    if (!formData.startDate) {
      toast.error('Start date is required');
      return;
    }
    if (formData.endDate && formData.endDate < formData.startDate) {
      toast.error('End date cannot be earlier than start date');
      return;
    }

    setSubmitting(true);
    try {
      const payload: Record<string, unknown> = {
        title: formData.title.trim(),
        description: formData.description.trim(),
        categoryId: formData.categoryId ? Number(formData.categoryId) : undefined,
        departmentId: formData.departmentId ? Number(formData.departmentId) : undefined,
        venueId: formData.venueId ? Number(formData.venueId) : undefined,
        startDate: formData.startDate,
        endDate: formData.endDate || formData.startDate,
        startTime: formData.startTime,
        endTime: formData.endTime,
        capacity: Number(formData.capacity) || 50,
        estimatedBudget: Number(formData.estimatedBudget) || 0,
        objectives: formData.objectives.trim(),
        requirements: formData.requirements.trim(),
        facultyCoordinator: formData.facultyCoordinator.trim(),
        studentCoordinator: formData.studentCoordinator.trim(),
      };

      await eventApi.create(payload);
      toast.success('Event proposal created successfully!');
      navigate('/events');
    } catch (err: any) {
      const msg = err.response?.data?.message || 'Failed to create event. Please check for scheduling conflicts.';
      toast.error(msg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={{ maxWidth: 840, margin: '0 auto', display: 'flex', flexDirection: 'column', gap: 20 }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
        <button onClick={() => navigate('/events')} className="btn btn-secondary btn-sm" style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
          <ArrowLeft size={16} /> Back to Events
        </button>
        <h1 className="page-title" style={{ margin: 0 }}>Create New Event</h1>
      </div>

      <div className="card" style={{ padding: 28 }}>
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
          {/* Title */}
          <div>
            <label style={{ display: 'block', fontWeight: 600, marginBottom: 6 }}>Event Title *</label>
            <input
              type="text"
              name="title"
              required
              value={formData.title}
              onChange={handleChange}
              placeholder="e.g. Annual Tech Symposium & Hackathon"
              style={{ width: '100%', padding: '10px 12px', borderRadius: 6, border: '1px solid var(--border)', background: 'var(--bg-secondary)', color: 'inherit' }}
            />
          </div>

          {/* Category & Department */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: 16 }}>
            <div>
              <label style={{ display: 'block', fontWeight: 600, marginBottom: 6 }}>Category</label>
              <select
                name="categoryId"
                value={formData.categoryId}
                onChange={handleChange}
                style={{ width: '100%', padding: '10px 12px', borderRadius: 6, border: '1px solid var(--border)', background: 'var(--bg-secondary)', color: 'inherit' }}
              >
                <option value="">Select Category</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>{c.name}</option>
                ))}
              </select>
            </div>

            <div>
              <label style={{ display: 'block', fontWeight: 600, marginBottom: 6 }}>Department</label>
              <select
                name="departmentId"
                value={formData.departmentId}
                onChange={handleChange}
                style={{ width: '100%', padding: '10px 12px', borderRadius: 6, border: '1px solid var(--border)', background: 'var(--bg-secondary)', color: 'inherit' }}
              >
                <option value="">Select Department</option>
                {departments.map((d) => (
                  <option key={d.id} value={d.id}>{d.name}</option>
                ))}
              </select>
            </div>
          </div>

          {/* Venue & Capacity */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: 16 }}>
            <div>
              <label style={{ display: 'block', fontWeight: 600, marginBottom: 6 }}>Venue</label>
              <select
                name="venueId"
                value={formData.venueId}
                onChange={handleChange}
                style={{ width: '100%', padding: '10px 12px', borderRadius: 6, border: '1px solid var(--border)', background: 'var(--bg-secondary)', color: 'inherit' }}
              >
                <option value="">Select Campus Venue</option>
                {venues.map((v) => (
                  <option key={v.id} value={v.id}>{v.name} (Capacity: {v.capacity})</option>
                ))}
              </select>
            </div>

            <div>
              <label style={{ display: 'block', fontWeight: 600, marginBottom: 6 }}>Max Capacity *</label>
              <input
                type="number"
                name="capacity"
                min={1}
                max={5000}
                required
                value={formData.capacity}
                onChange={handleChange}
                style={{ width: '100%', padding: '10px 12px', borderRadius: 6, border: '1px solid var(--border)', background: 'var(--bg-secondary)', color: 'inherit' }}
              />
            </div>
          </div>

          {/* Dates & Times */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: 16 }}>
            <div>
              <label style={{ display: 'block', fontWeight: 600, marginBottom: 6 }}>Start Date *</label>
              <input
                type="date"
                name="startDate"
                required
                value={formData.startDate}
                onChange={handleChange}
                style={{ width: '100%', padding: '10px 12px', borderRadius: 6, border: '1px solid var(--border)', background: 'var(--bg-secondary)', color: 'inherit' }}
              />
            </div>

            <div>
              <label style={{ display: 'block', fontWeight: 600, marginBottom: 6 }}>End Date</label>
              <input
                type="date"
                name="endDate"
                value={formData.endDate}
                onChange={handleChange}
                style={{ width: '100%', padding: '10px 12px', borderRadius: 6, border: '1px solid var(--border)', background: 'var(--bg-secondary)', color: 'inherit' }}
              />
            </div>

            <div>
              <label style={{ display: 'block', fontWeight: 600, marginBottom: 6 }}>Start Time</label>
              <input
                type="time"
                name="startTime"
                value={formData.startTime}
                onChange={handleChange}
                style={{ width: '100%', padding: '10px 12px', borderRadius: 6, border: '1px solid var(--border)', background: 'var(--bg-secondary)', color: 'inherit' }}
              />
            </div>

            <div>
              <label style={{ display: 'block', fontWeight: 600, marginBottom: 6 }}>End Time</label>
              <input
                type="time"
                name="endTime"
                value={formData.endTime}
                onChange={handleChange}
                style={{ width: '100%', padding: '10px 12px', borderRadius: 6, border: '1px solid var(--border)', background: 'var(--bg-secondary)', color: 'inherit' }}
              />
            </div>
          </div>

          {/* Budget */}
          <div>
            <label style={{ display: 'block', fontWeight: 600, marginBottom: 6 }}>Estimated Budget (₹)</label>
            <input
              type="number"
              name="estimatedBudget"
              min={0}
              step={500}
              value={formData.estimatedBudget}
              onChange={handleChange}
              style={{ width: '100%', padding: '10px 12px', borderRadius: 6, border: '1px solid var(--border)', background: 'var(--bg-secondary)', color: 'inherit' }}
            />
          </div>

          {/* Description */}
          <div>
            <label style={{ display: 'block', fontWeight: 600, marginBottom: 6 }}>Event Description</label>
            <textarea
              name="description"
              rows={4}
              value={formData.description}
              onChange={handleChange}
              placeholder="Outline event agenda, speaker highlights, and participation guidelines..."
              style={{ width: '100%', padding: '10px 12px', borderRadius: 6, border: '1px solid var(--border)', background: 'var(--bg-secondary)', color: 'inherit', resize: 'vertical' }}
            />
          </div>

          {/* Coordinators */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: 16 }}>
            <div>
              <label style={{ display: 'block', fontWeight: 600, marginBottom: 6 }}>Faculty Coordinator</label>
              <input
                type="text"
                name="facultyCoordinator"
                value={formData.facultyCoordinator}
                onChange={handleChange}
                placeholder="e.g. Dr. A. Sharma"
                style={{ width: '100%', padding: '10px 12px', borderRadius: 6, border: '1px solid var(--border)', background: 'var(--bg-secondary)', color: 'inherit' }}
              />
            </div>

            <div>
              <label style={{ display: 'block', fontWeight: 600, marginBottom: 6 }}>Student Coordinator</label>
              <input
                type="text"
                name="studentCoordinator"
                value={formData.studentCoordinator}
                onChange={handleChange}
                placeholder="e.g. Alex Mercer"
                style={{ width: '100%', padding: '10px 12px', borderRadius: 6, border: '1px solid var(--border)', background: 'var(--bg-secondary)', color: 'inherit' }}
              />
            </div>
          </div>

          <div style={{ display: 'flex', gap: 12, justifyContent: 'flex-end', marginTop: 12 }}>
            <button type="button" onClick={() => navigate('/events')} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" disabled={submitting} className="btn btn-primary">
              {submitting ? 'Creating Event...' : 'Create Event Proposal'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
