// Shared contract for anything a Tile can hold: a living Individual or an
// inert Obstacle. Tile only needs to know "what symbol do I show," not which
// concrete type it's holding - that's polymorphism doing the work again.
public interface Occupant {
    char getSymbol();
}
