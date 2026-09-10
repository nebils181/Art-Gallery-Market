# Art Gallery Market

A Java Swing desktop application for managing an art gallery's inventory, customers, and sales.
(A console version is also included — see "Console version" below.)

## Features (as requested)
- **Artwork tracking**: title, artist, medium, year, price, availability status
- **Customer records**: name, phone, email
- **Purchase processing**: links a customer to an artwork, records sale price and timestamp, updates availability
- **Availability tracking**: AVAILABLE / RESERVED / SOLD status on every artwork

## Extra features added
- **Reservations**: hold a piece before finalizing a sale (common in real galleries)
- **Search**: find artworks by title or artist keyword; find customers by name
- **Price range filter** (`getArtworksInPriceRange`) for browsing by budget
- **Purchase history per customer**
- **Sales reporting**: total revenue, counts by status, top-selling artists
- **Custom exceptions** (`RecordNotFoundException`, `ArtworkNotAvailableException`) instead of silent failures or generic exceptions, so bad input (unknown id, buying something already sold) is reported clearly
- Sample data is pre-loaded on startup so you can try it immediately

## Structure
```
src/com/artgallery/
  model/       Artwork, Customer, Purchase, ArtworkStatus (enum)
  service/     ArtGalleryService — all business logic & in-memory storage
  exception/   RecordNotFoundException, ArtworkNotAvailableException
  gui/         Swing GUI: MainFrame + one panel & table model per tab
  Main.java    GUI entry point
  CliMain.java Console entry point (original menu-driven version, still works)
```

The design deliberately separates **model** (data), **service** (business rules —
the only place that mutates state), and the UI layer (`gui/` or `CliMain`). This
means swapping the in-memory `HashMap` storage in `ArtGalleryService` for a real
database later only requires changing that one class — neither UI needs to change.

## Build & run (GUI)
Requires a JDK with AWT/Swing support (the full JDK, not a "headless" package).

```bash
# from the project root
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out com.artgallery.Main
```

This opens a window with four tabs:
- **Artworks** — insert new artwork, search/filter, reserve or delete a selected piece
- **Customers** — add a customer, view the customer list
- **Sales** — pick an artwork + customer from dropdowns and complete a purchase; view sale history
- **Dashboard** — live inventory counts, total revenue, sales by artist

Sample data is preloaded so you can try it immediately.

## Console version
The original command-line menu is still available:
```bash
java -cp out com.artgallery.CliMain
```

## Possible next steps
- Persist data to a file or database (the service layer is already isolated for this)
- Add authentication for gallery staff vs. customers
- Support returns/cancellations
- Add artwork images (e.g. a thumbnail column/preview panel)
- Package as a runnable .jar with a manifest, or wrap as a REST API
