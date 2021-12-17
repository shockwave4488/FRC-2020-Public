# Git #

Git is the ultimate in source control - gone are the days of making copies of a
folder structure just to preserve history.

With all its wonder, there is a TON of different strategies and corner cases
for using it. It will let you nearly blow your feet off if you let it, but with
all the power, comes a very sophisticated mechanism for watching changes over
time and working with multiple people on the same code base.

# Rebase vs Squash merging #
First lets go over the two primary strategies in git to deal with multiple
branches with many developers.

Merge commits are git's (default) messy way of coping with multiple
users or branches becoming intertwined once they are pushed to the master
branch. They are not necessarily "bad", but they do muddy up the waters
slightly when looking at history, especially when you want to keep a feature
clean and tidy.

Rebasing, in simple terms, is re-writing history. If developer A creates a branch
off master, followed by developer B pushing their code into master and
developer A trying to merge theirs in after that, you are left with a question
- where in time does developer A's code actually live? Is it more prudent for
it to have been created before developer B, or afterwards?...

Rebasing answers that quetion by saying that developer A's code was created
after developer B. It re-writes history by pulling A out of time and placing it
forward as if they had written it after B. This can be helpful for keeping nice
pretty git history, one that is not all gunked up with merge commits.

Squash commits, unlike rebase commits or merge commits, reduce all commits
related to a feature down to one. When a feature branch is finished and 
reviewed, the squash process will take all commits from the feature branch
and make a single commit with all changes (essentially grabbing only the latest
commit from the branch), and merges it into the master branch directly ahead of
the previous commit. This results in a cleaner history, but destroys the history
and progress of the feature through the various commits on its branch.

# Basic Workflow with Squash & Merge #
The simplest way to work with our git repository (read: least likely to
cause merge conflicts) is to follow these steps when developing code:
1. Ensure the master branch is checked out and unmodified (run "git status"
    to confirm)
2. Execute "git pull" to get all of the latest updates from master
3. Make a new branch for your feature: "git checkout -b new_branch_name"
4. Develop and test your code.
5. Make a commit (you can also make multiple commits along the way): "git commit"
6. You can save your code by pushing the code to GitHub: execute
    "git push origin new_branch_name" to do so.
    This also allows others to see and contribute to your feature branch.
7. Once you have developed and tested your feature, do a final push to GitHub.
8. Open a pull request to have others review your code. If changes are requested,
    develop, modify, and test new code to close the review. Once the reviews are
    complete, your new branch will be squashed and merged into the master branch!
9. Once you have a new feature to work on, start over from step 1.
