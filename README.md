# c-means

The dataset used is Irish Fisher.

Description of the algorithm:
        1. We distribute the points into clusters randomly.
        2. For each cluster, we find its center, for this we consider the component-by-component
        average of all points related to it, and each point is taken with a weight
        equal to the degree of its belonging to the cluster.
        3. Recalculate the distribution matrix according to the following formula.
        4. If at the previous step the distribution matrix has changed by less than ϵ,
        or if we have completed the maximum allowable number of iterations, we stop
        working. Otherwise, we proceed to point 2.
        5. The result of the algorithm is the current splitting of points into clusters.
