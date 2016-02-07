package es.uam.eps.ir;

import java.util.Set;


public interface Dataset {

    public Set<Long> getUsers(Long item);

    public Set<Long> getItems(Long user);

    public Set<Long> getUsers();

    public Set<Long> getItems();

    public int getNumRatings();

    public double getRating(Long user, Long item);

    public double getMaxRating();
}
