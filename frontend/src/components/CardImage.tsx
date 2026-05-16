interface CardImageProps {
  imageUri?: string;
  name: string;
  style?: React.CSSProperties;
}

export function CardImage({ imageUri, name, style }: CardImageProps) {
  if (imageUri) {
    return (
      <img
        src={imageUri}
        alt={name}
        className="card-img"
        style={style}
        loading="lazy"
        onError={e => { (e.target as HTMLImageElement).style.display = 'none'; }}
      />
    );
  }
  return (
    <div
      className="card-img"
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        fontSize: '0.75rem',
        color: 'var(--text-muted)',
        padding: '0.5rem',
        textAlign: 'center',
        ...style,
      }}
    >
      {name}
    </div>
  );
}
