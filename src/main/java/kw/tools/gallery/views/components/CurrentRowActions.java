package kw.tools.gallery.views.components;

/**
 * Communication interface of component to main grid view.
 */
public interface CurrentRowActions
{
    void nextPage();

    void filter(String key, String value);

    void hardDelete();

    void setRating(int rating);

    class NoOp implements CurrentRowActions
    {
        @Override
        public void nextPage()
        {
        }

        @Override
        public void filter(String key, String value)
        {
        }

        @Override
        public void hardDelete()
        {

        }

        @Override
        public void setRating(int rating)
        {

        }
    }
}
