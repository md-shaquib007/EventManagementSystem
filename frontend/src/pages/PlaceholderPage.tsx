export default function PlaceholderPage({ title }: { title: string }) {
  return (
    <div>
      <h1 className="page-title">{title}</h1>
      <div className="card" style={{ textAlign: 'center', padding: 60 }}>
        <p style={{ color: 'var(--text-secondary)' }}>
          This module is connected to the backend API. Extend the UI components here.
        </p>
      </div>
    </div>
  );
}
