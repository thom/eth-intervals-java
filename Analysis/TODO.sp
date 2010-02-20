___ To Do List _______________________________________________________

______ Bugs __________________________________________________________   

- Improve enforcement of constraint that an interval's object constructor
  is always completed before it executes.  
  
  Currently we prohibit non-default ctors when instantiating an interval
  subtype.  This is unsatisfyingly restrictive.
  
  Not sure what is the best alternative. One thought is to require that the end of the
  interval's object ctor always //happens before// the end of the method or the enclosing
  subinterval: This works if eliminate explicit calls to {schedule()}, as the interval
  will only be scheduled when the {run()} method which created it returns. Unsatisfying
  because it distinguishes reified subintervals somewhat, but perhaps that's ok.

- Update type of f_objCtor from t_interval to something which requires
  that the interval ctor has completed, at least.  Or is that necessary?
  
- Extend canonical paths to allow fields of ghosts: we can use the
  class of the ghost type to determine what ghost/reified field exist,
  and can always produce a class lower-bound for the type.
  
- Require that a bound ghost object of type c be a subtype of
  {c @Constructor[c](hbNow)}?  In other words, //at least// the
  class {c} must be fully constructed.
  
- Disallow a field f with a path type whose path is "this.f"

- Introduce immutableIn to supplement readableBy?

  This used to be covered by <f: hb this>.  An alternative would be
  to keep "hb this" but just not add it to the HB relation.
  
- Check type args in wf check

- Check that there are no fields named f_objCtor declared anywhere

- In addHb(), check that the arguments are immutable but also that their
  constructors have completed.

- Rewrite type checker to (a) expand supertype into a set of lower-bounds
  and (b) expand subtype to a set of upper-bounds.  In this way we avoid 
  infinite recursion and also handle equivalent types like v1<E: v2.E>
  v2<E: v1.E> nicely.

- Check in multiple inheritance that all paths lead to the same set of ghost parameters.
  Alternatively, make sure that all paths are fulfilled. Latter technique is "cooler" (then, for
  ex., a class modelling an empty set can fulfill virtually any type, etc)
    
- Sanity check casts or otherwise restrict them?

- Circular canonical paths

  [[@test-plugin/src/basic/CircularGhostA.java]] should conclude that the circular ghosts
  are equal (which it doesn't do now).
  
  Three possibilities:
  - The canon() function could return a set of canonical paths.
  - A {CanonPath} instance could be a set of path, type pairs.  
  - Use some strategy to pick "the best" of the options in the cycle.  Prefer reified with
    more specific types, longer paths, something like that.
  The last is clearly the least disruptive.

- More sophisticated merging with respect to temp/perm

  If all preds have the same effective mapping, but some in temp and some in pred, the succ should
  have the mapping in temp. For example:
  {
      if(...) {
          b = ...; // immutable object
          a = b.f; // permanent!
      } else {
          b = ...; // mutable object
          a = b.f; // temporary
      }
      // safe if b.f→a temporarily        
  }
 
______ Features ______________________________________________________

- {@Is} annotations on methods and variable declarations

- Assertions

  || a hb b              | user's pnt.hb(pnt) ||
  || a locks b           | user's foo.holdsLock(bar) ||
  || a readableBy b      | user's foo.isReadable() translates to "foo readableBy method" ||
  || a writableBy b      | user's foo.isWritable() translates to "foo writableBy method" ||
  || a == b              | creates a temporary alias from b to a? ||
  || a == b->p           | creates a temporary alias from b->p to a ||

- Javac plugin

- Requires in class bodies, declarations.

  If a class declared, for example, that two of its fields f and g have a HB relationship,
  then this would link f and g.  We can use the existing linked fields mechanisms to ensure
  that they are written together.
    
- Static fields, methods

______ Translator Features ___________________________________________

- User-specifiable primary guard

- Defaults for bound ghosts or just unbound ghosts?

- Allow user specified defaults.  

  Perhaps something like:
  {
      @Defaults({
          @Default(Foo.class, "this.some.path")
      })
      @Foo class Class {
      }      
  }
    
- Improve parsing of annty to allow for annotations, imports, etc

______ Things to Think About _________________________________________

- Remove SSA?  Or somehow fix branch/continue type checking?

  Right now we don't type check the phi node assignments in SSA.  This is
  because we do copy propagation in TranslateMethodBody, so that when there 
  is a bad assignment x = y where y is the wrong type, this gets reported 
  at every Phi node, which is a bit much.  To fix this however would require adding
  Copy statements to the IR --- and if we do that, I have a feeling it would be
  better to get rid of SSA altogether.

- Replace constructors with static methods, moving constructors into translator

- hb relations to arbitrary Guards

  Right now something like {@Creator("hb this")} doesn't work because Creator is 
  typed as Guard, not Interval, and we can't add hb relations to something of type
  Guard.  This tripped me up for a while on Producer (particularly since no error
  message was generated).
  
  It would be nice to allow this: after all, the only kind of Guard it could
  be, in that case, is an Interval, and it couldn't have been stored if it didn't
  meet the criteria.
  
  For now, however, I made the Producer example pass by doing {@Creator("readableBy this")}.
  This is arguably better anyhow.

- Package-Level Ghosts

  Before we found the idea of package-level regions quite useful.  Are they still useful?
  How do they fit in with ghosts-as-fields?  Could have a synthetic interface for every package.
  Each class implements the interface for the package in which it is declared.  Seems
  like it could work.
        
- Allow multiple guards (or a more complex guard predicate of some kind)

  For example, lock AND written during.  This way, you could have shared data accessed by
  locks up to a point, where it becomes read-only without locks.

- Ensures declarations

  In place of the current inference, we should add ensures declarations on constructors
  (or potentially other methods).  These ensures declarations could be inferred by the
  compiler for constructors as we do today, but could also be manually specified to support
  virtual methods and helpers for constructors.
    
- Iterated linked fields

  Right now we screen out linked fields whose guards are known not to
  have happened \-\-- but can we also screen out fields whose guards are
  themselves guarded by intervals that have not happened?  Maybe???
  Does that even make sense????
    
- Ordering among fields, parameters, ghosts?  

  We used to have some code that prevented the types of ghosts, fields, method
  parameters, etc from referencing later declarations.  I am not sure
  if there is any good reason for this code.  It may prevent infinite cycles 
  in the type check but I'm not sure where.

- Check types of arguments to a type when checking for WF?
    
  Right now, we only check that the ghost arguments on a type 
  are well-typed when checking a "new" statement.  This should be
  sufficient as it would be impossible to ever store a value (other
  than null) into an ill-typed variable, since you could never 
  create an object with that type.

- Fields that are modified outside of constructor?

  In the classes Indirect2/Indirect3 defined in the test illegalLinkedFields, 
  we see that indirect dependencies in types are generally disallowed
  but for when we can guarantee that the dependent field is never modified
  until the dependee is constant.  Right now that is only permitted in one case:
  we know that this.constructor hb this if this is an interval.  
  
  In some cases, if/when we add the ability to add more complex requirements,
  we could add have a field f guarded by inter1 and a dependent field g guarded by inter2
  where inter1 hb inter2.  (inter1 would typically be this.constructor)
  
  What might also be desirable, but probably only rarely, would be 
  some way to indicate that a field is not assigned until outside of the contructor.
  This would allow us to treat this.ctor fields as constant.  Hmm, when I started
  writing it, this seemed common, but now I think it probably isn't.