# Idea

This tool must allow easy browsing through many galleries of pictures.
Each gallery will usually contain only up to several pictures but there can be up to thousands of galleries.
The tool must allow some basic operations on the galleries: to throw it away or categorize.

# Specifications

### Viewing

There is a list of all repositories. Repository contains galleries.
Some statistics could be displayed for each repository.

A "gallery" is a folder directly containing images.
Folders with images can be nested, but each of them is a separate gallery.

Can open a repository to view all galleries in any of two modes:
- Single-image - is a big picture containing many thumbnails. It is viewed as a single block.
- Multi image - some thumbnails from the gallery are displayed. It is viewed as a row.

Any image from any mode can be opened in full size.

Can also open a gallery to view all images in full size.

Gallery list in the repository could be filtered by name, rating or tags.
Gallery list in the repository could be sorted by name, rating or tags.

An operation could be executed upon the filtered result:
- move to another directory
- set rating, tags, name
- remove

### Managing repositories
  
Can add a new repository by entering a physical localization. 
The localization can be absolute or relative.

Initial name is set to the directory name.

After adding a repository, it is cached: two types of thumbnails are generated.
Some sort of progress bar could be displayed while processing.

It is possible to remove a repository. The related cache is then removed.
All the data of the galleries is removed.

Repository can be refreshed to scan for changes:
- find new galleries and generate initial data for them
- notice lost galleries and remove their cache and data
- do not re-generate thumbnails for existing galleries, even when images changed
- **DO** re-generate thumbnails even for existing galleries if configuration option changed
- some sort of progress bar could be displayed


### Managing galleries

A gallery can be given a name.
Some other values could be assigned to a gallery, for example rating or tags.

A gallery can be removed **from disk**. Its files, folder, cache and all DB data is then removed. 
This is not undoable.

### Configuration

Number of thumbnails on single-image.

Single-image dimensions. Thumbnails will be fit accordingly.

Number of thumbnails in multi-image (row) view.

Dimensions of thumbnails in multi-image view.

Configuration is global for all repositories. 
(Optional feature: can be overriden for repository)
