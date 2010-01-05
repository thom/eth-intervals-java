package ch.ethz.intervals

// First pass-- basic sanity checks.
class WfCheck(val prog: Prog) 
extends TracksEnvironment(prog) {
    import prog.log
    import prog.classDecl
    import prog.at
    import prog.isSubclass
    import prog.ghostFieldDecls
    import prog.thisTref
    import prog.typeOriginallyDefiningMethod
        
    // In this check, we don't record HB information,
    // so we permit mutable paths at all places:
    val ghostOk = ir.ghostAttrs.withMutable
    val reified = ir.mutableAttrs
    
    def checkIsSubclass(wt: ir.WcTypeRef, cs: ir.ClassName*) {
        if(!cs.exists(isSubclass(wt, _)))
            throw new ir.IrError("intervals.expected.subclass.of.any", wt.c, cs)
    }
    
    def checkPathWfAndSubclass(as: ir.Attrs, p: ir.Path, cs: ir.ClassName*): Unit = {
        val tp = teePee(as, p)
        checkIsSubclass(tp.wt, cs: _*)
    }
            
    def checkPathsWfAndSubclass(as: ir.Attrs, ps: List[ir.Path], cs: ir.ClassName*): Unit =
        ps.foreach(checkPathWfAndSubclass(as, _, cs: _*))
    
    def checkPathWf(as: ir.Attrs, p: ir.Path): Unit =
        teePee(as, p)        

    def checkPathsWf(as: ir.Attrs, ps: List[ir.Path]): Unit =
        teePee(as, ps)        
        
    def checkWPathWf(wp: ir.WcPath): Unit =
        wp match {
            case ir.WcReadableBy(ps) =>
                checkPathsWfAndSubclass(ghostOk, ps, ir.c_interval)
                
            case ir.WcWritableBy(ps) =>
                checkPathsWfAndSubclass(ghostOk, ps, ir.c_interval)
                
            case ir.WcHb(ps, qs) =>
                checkPathsWfAndSubclass(ghostOk, ps, ir.c_interval, ir.c_point)
                checkPathsWfAndSubclass(ghostOk, qs, ir.c_interval, ir.c_point)
                
            case ir.WcLocks(ps) =>
                checkPathsWfAndSubclass(ghostOk, ps, ir.c_lock)
                
            case ir.WcLockedBy(ps) =>
                checkPathsWfAndSubclass(ghostOk, ps, ir.c_interval)
                
            case p: ir.Path =>
                checkPathWf(ghostOk, p)
        }
        
    def checkLengths(l1: List[_], l2: List[_], msg: String) = preservesEnv {
        if(l1.length != l2.length)
            throw new ir.IrError(msg, l1.length, l2.length)
    }
    
    def checkWfWt(wt: ir.WcTypeRef) {
        wt.wghosts.foldLeft(List[ir.FieldName]()) {
            case (l, wg) if l.contains(wg.f) => throw new ir.IrError("intervals.duplicate.ghost", wg.f)
            case (l, wg) => wg.f :: l
        }
        
        wt.wghosts.map(_.wp).foreach(checkWPathWf)

        val lgfd = ghostFieldDecls(wt.c)
        def notDefined(wg: ir.WcGhost) = !lgfd.exists(_.name == wg.f)        
        wt.wghosts.find(notDefined) match {
            case Some(wg) => throw new ir.IrError("intervals.no.such.ghost", wt.c, wg.f)
            case None =>
        }

        // Note: we don't check that the arguments
        // match the various ghost bounds etc.  We just
        // check that when the type is constructed.
    }
    
    def checkCall(tp: ir.TeePee, msig: ir.MethodSig, tqs: List[ir.TeePee]) {
        checkLengths(msig.args, tqs, "intervals.wrong.number.method.arguments")        
    }

    def checkStatement(stmt: ir.Stmt): Unit =
        at(stmt, ()) {
            stmt match {                  
                case ir.StmtSuperCtor(m, qs) =>
                    val tp = tp_super
                    val tqs = teePee(ir.noAttrs, qs)
                    val msig_ctor = substdCtorSig(tp, m, tqs)
                    checkCall(tp, msig_ctor, tqs)
                    
                case ir.StmtGetField(x, p_o, f) =>
                    val tp_o = teePee(reified, p_o)
                    substdFieldDecl(tp_o, f) match {
                        case ir.GhostFieldDecl(_, _) =>
                            throw new ir.IrError("intervals.not.reified", tp_o.wt.c, f)
                        
                        case ir.ReifiedFieldDecl(_, wt, _, p_guard) =>
                            addLvDecl(x, wt, None)
                    }                    
                    
                case ir.StmtSetField(p_o, f, p_v) =>
                    checkPathWf(reified, p_v)
                    
                    val tp_o = teePee(reified, p_o)
                    substdFieldDecl(tp_o, f) match {
                        case ir.GhostFieldDecl(_, _) =>
                            throw new ir.IrError("intervals.not.reified", tp_o.wt.c, f)
                        
                        case ir.ReifiedFieldDecl(_, _, _, _) =>
                    }

                case ir.StmtCall(x, p, m, qs) =>
                    val tp = teePee(reified, p)
                    val tqs = teePee(reified, qs)
                    val msig = substdMethodSig(tp, m, tqs)
                    checkCall(tp, msig, tqs)
                    addLvDecl(x, msig.wt_ret, None)                        
        
                case ir.StmtSuperCall(x, m, qs) =>
                    val tp = tp_super
                    val tqs = teePee(reified, qs)
                    val msig = substdMethodSig(tp, m, tqs)
                    checkCall(tp, msig, tqs)
                    addLvDecl(x, msig.wt_ret, None)                        
                
                case ir.StmtNew(x, t, m, qs) =>
                    if(classDecl(t.c).attrs.interface)
                        throw new ir.IrError("intervals.new.interface", t.c)
                    checkWfWt(t)
                    checkPathsWf(ghostOk, t.ghosts.map(_.p))
                    val tqs = teePee(reified, qs)
                    
                    // Check that all ghosts on the type C being instantiated are given a value:
                    prog.ghostFieldDecls(t.c).find(f => t.oghost(f.name).isEmpty) match {
                        case Some(f) => throw new ir.IrError("intervals.no.value.for.ghost", f)
                        case None =>
                    }
                    
                    addLvDecl(x, t, None)
                    
                    val tp_x = teePee(x.path)
                    val msig_ctor = substdCtorSig(tp_x, m, tqs)
                    checkCall(tp_x, msig_ctor, tqs)                    
                    
                case ir.StmtCast(x, wt, p) => 
                    checkPathWf(reified, p)
                    checkWfWt(wt)
                    addLvDecl(x, wt, None)
                    
                case ir.StmtNull(x, wt) => 
                    checkWfWt(wt)
                    addLvDecl(x, wt, None)
                    
                case ir.StmtReturn(p) =>
                    teePee(reified, p)
                    
                case ir.StmtHb(p, q) =>                
                    checkPathWfAndSubclass(reified, p, ir.c_point, ir.c_interval)
                    checkPathWfAndSubclass(reified, q, ir.c_point, ir.c_interval)
                    
                case ir.StmtLocks(p, q) =>
                    checkPathWfAndSubclass(reified, p, ir.c_interval)
                    checkPathWfAndSubclass(reified, q, ir.c_lock)
                    
                case ir.StmtSubintervalPush(x, addCheckedArglocks) =>
                    checkPathsWfAndSubclass(reified, addCheckedArglocks, ir.c_lock)                    
                    addLvDecl(x, ir.t_interval, None)                    
                    
                case ir.StmtSubintervalPop(x) =>
                    checkPathWfAndSubclass(reified, x.path, ir.c_interval)
            }        
        }
    
    def checkStatements(stmts: List[ir.Stmt]): Unit =
        stmts.foreach(checkStatement)
    
    def addCheckedArg(arg: ir.LvDecl) {
        checkWfWt(arg.wt) 
        addArg(arg)
    }
    
    def checkReq(req: ir.Req) =
        at(req, ()) {
            req match {
                case ir.ReqWritableBy(ps, qs) =>
                    checkPathsWfAndSubclass(ghostOk, ps, ir.c_guard)
                    checkPathsWfAndSubclass(ghostOk, qs, ir.c_interval)
                case ir.ReqReadableBy(ps, qs) => 
                    checkPathsWfAndSubclass(ghostOk, ps, ir.c_guard)
                    checkPathsWfAndSubclass(ghostOk, qs, ir.c_interval)
                case ir.ReqHb(ps, qs) => 
                    checkPathsWfAndSubclass(ghostOk, ps, ir.c_point, ir.c_interval)
                    checkPathsWfAndSubclass(ghostOk, qs, ir.c_point, ir.c_interval)
                case ir.ReqSubintervalOf(ps, qs) => 
                    checkPathsWfAndSubclass(ghostOk, ps, ir.c_interval)
                    checkPathsWfAndSubclass(ghostOk, qs, ir.c_interval)
            }   
        }
    
    def checkGoto(blks: Array[ir.Block], succ: ir.Goto) =
        at(succ, ()) {
            if(succ.b < 0 || succ.b >= blks.length)
                throw new ir.IrError("intervals.invalid.blk.id", succ.b)
            checkLengths(blks(succ.b).args, succ.ps, "intervals.succ.args")
            checkPathsWf(reified, succ.ps)        
        }
    
    def checkBlock(blks: Array[ir.Block], b: Int) =
        log.indented("%s: %s", b, blks(b)) {
            savingEnv {
                blks(b).args.foreach(addCheckedArg)                
                checkStatements(blks(b).stmts)
                blks(b).gotos.foreach(checkGoto(blks, _))
            }
        }
        
    def checkNoninterfaceMethodDecl(
        cd: ir.ClassDecl,          // class in which the method is declared
        md: ir.MethodDecl          // method to check
    ) = 
        at(md, ()) {
            savingEnv {
                // Define special vars "method" and "this":
                addPerm(ir.lv_mthd, ir.TeePee(ir.t_interval, ir.p_mthd, ir.ghostAttrs))
                if(!md.attrs.ctor) { 
                    // For normal methods, type of this is the defining class
                    addPerm(ir.lv_this, ir.TeePee(thisTref(cd), ir.p_this, ir.noAttrs))                     
                } else {
                    // For constructor methods, type of this is the type that originally defined it
                    val t_rcvr = typeOriginallyDefiningMethod(cd.name, md.name).get
                    addPerm(ir.lv_this, ir.TeePee(t_rcvr.ctor, ir.p_this, ir.noAttrs))
                }  

                withCurrent(ir.p_mthd) {
                    md.args.foreach(addCheckedArg)
                    checkWfWt(md.wt_ret)                
                    md.reqs.foreach(checkReq)      
                    md.blocks.indices.foreach(checkBlock(md.blocks, _))                    
                }
            }         
        }
        
    def checkNoninterfaceConstructorDecl(cd: ir.ClassDecl, md: ir.MethodDecl): Unit = 
        at(md, ()) {
            savingEnv {
                // Define special vars "method" (== this.constructor) and "this":
                addPerm(ir.lv_mthd, ir.TeePee(ir.t_interval, ir.gfd_ctor.thisPath, ir.ghostAttrs))
                addPerm(ir.lv_this, ir.TeePee(thisTref(cd, ir.ctorAttrs), ir.p_this, ir.noAttrs))

                withCurrent(ir.p_mthd) {
                    md.args.foreach { case arg => 
                        checkWfWt(arg.wt) 
                        addPerm(arg.name, ir.TeePee(arg.wt, arg.name.path, ir.noAttrs))
                    }            
                    md.reqs.foreach(checkReq)
            
                    // TODO -- Have the checking of super() verify that p_this is a ghost
                    // and reify it, and thus permit control-flow.  We then have to propagate
                    // p_this between blocks though (yuck).
            
                    md.blocks.indices.foreach(checkBlock(md.blocks, _))
                }
            }          
        }
        
    def addThis(cd: ir.ClassDecl, attrs: ir.Attrs) {
        val t_this = thisTref(cd, attrs)
        val tp_this = ir.TeePee(t_this, ir.p_this, ir.noAttrs)
        addPerm(ir.lv_this, tp_this)        
    }
    
    def checkReifiedFieldDecl(cd: ir.ClassDecl, fd: ir.ReifiedFieldDecl): Unit = 
        at(fd, ()) {
            savingEnv {
                addThis(cd, ir.ctorAttrs)
                
                checkWfWt(fd.wt)
                checkPathWf(ghostOk, fd.p_guard)
            }
        }
    
    def checkGhostFieldDecl(cd: ir.ClassDecl, gfd: ir.GhostFieldDecl): Unit = 
        at(gfd, ()) {
            savingEnv {
                addThis(cd, ir.ctorAttrs)

                // Check that ghosts are not shadowed from a super class:                
                prog.strictSuperclasses(cd.name).foreach { c =>
                    if(classDecl(c).fields.exists(_.name == gfd.name))
                        throw ir.IrError("intervals.shadowed.ghost", c, gfd.name)
                }
                
                checkWfWt(gfd.wt)
            }
        }
        
    def checkFieldDecl(cd: ir.ClassDecl)(priorNames: Set[ir.FieldName], fd: ir.FieldDecl) = 
        at(fd, priorNames) {
            if(priorNames(fd.name))
                throw new ir.IrError("intervals.duplicate.field", fd.name)
            fd match {
                case rfd: ir.ReifiedFieldDecl => checkReifiedFieldDecl(cd, rfd)
                case gfd: ir.GhostFieldDecl => checkGhostFieldDecl(cd, gfd)
            }
            priorNames + fd.name
        }
        
    // ___ Classes and interfaces ___________________________________________
    
    def checkIsInterface(c: ir.ClassName) {
        val cd = classDecl(c)
        if(!cd.attrs.interface) 
            throw new ir.IrError("intervals.superType.not.interface", c)
    }
    
    def checkInterfaceSuperclass(c: ir.ClassName) {
        val cd = classDecl(c)
        if(c != ir.c_object) checkIsInterface(c)
    }
    
    def checkInterfaceConstructorDecl(cd: ir.ClassDecl, md: ir.MethodDecl) =
        at(md, ()) {
            if(!md.blocks.deepEquals(ir.md_ctor_interface.blocks))
                throw new ir.IrError("intervals.invalid.ctor.in.interface")
            // TODO Check other parts of constructor signature. (Very low priority)
        }
    
    def checkInterfaceMethodDecl(cd: ir.ClassDecl, md: ir.MethodDecl) =
        checkNoninterfaceMethodDecl(cd, md)
    
    def checkInterfaceClassDecl(cd: ir.ClassDecl) =
        at(cd, ()) {
            savingEnv {
                // TODO Is this everything?
                cd.superClasses.foreach(checkInterfaceSuperclass)
                if(!cd.fields.isEmpty)
                    throw new ir.IrError("intervals.interface.with.fields")
                cd.ctors.foreach(checkInterfaceConstructorDecl(cd, _))
                cd.methods.foreach(checkInterfaceMethodDecl(cd, _))
            }
        }
        
    def checkIsNotInterface(c: ir.ClassName) {
        val cd_super = classDecl(c)
        if(cd_super.attrs.interface) 
            throw new ir.IrError("intervals.superType.interface", c)
    }

    def checkNoninterfaceClassDecl(cd: ir.ClassDecl) = 
        at(cd, ()) {
            savingEnv {
                cd.superClasses.take(1).foreach(checkIsNotInterface)
                cd.superClasses.drop(1).foreach(checkIsInterface)
                cd.fields.foldLeft(Set.empty[ir.FieldName])(checkFieldDecl(cd))
                cd.ctors.foreach(checkNoninterfaceConstructorDecl(cd, _))
                cd.methods.foreach(checkNoninterfaceMethodDecl(cd, _))                    
            }
        }
        
    def checkClassDecl(cd: ir.ClassDecl) = log.indented(cd) {        
        if(cd.attrs.interface) checkInterfaceClassDecl(cd)
        else checkNoninterfaceClassDecl(cd)
    }        
}