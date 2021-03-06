package ch.ethz.intervals

import Util._

// First pass-- basic sanity checks.
class WfCheck(prog: Prog) extends TracksEnvironment(prog) 
{
    import prog.logStack.log
    import prog.logStack.indexLog
    import prog.logStack.at
    
    def canonPath = env.canonPath _
    def canonLv = env.canonLv _
    def reifiedPath = env.reifiedPath _
    def reifiedLv = env.reifiedLv _

    def checkCouldHaveClass(cp: ir.CanonPath, cs: ir.ClassName*) {
        if(!cs.exists(env.pathCouldHaveClass(cp, _)))
            throw new CheckFailure("intervals.expected.subclass.of.any", cp.reprPath, ", ".join(cs))
    }
    
    def checkSubclass(cp: ir.CanonPath, cs: ir.ClassName*) {
        if(!cs.exists(env.pathCouldHaveClass(cp, _)))
            throw new CheckFailure("intervals.expected.subclass.of.any", cp.reprPath, ", ".join(cs))
    }
    
    def checkCanonAndSubclass[X](
        canonizer: (X => ir.CanonPath),
        arg: X, 
        cs: ir.ClassName*
    ): Unit = {
        val cp = canonizer(arg)
        checkSubclass(cp, cs: _*)
    }
            
    def checkCanonsAndCouldHaveClass[X](
        canonizer: (X => ir.CanonPath),        
        ps: List[X], 
        cs: ir.ClassName*
    ): Unit = {
        ps.foreach { p =>
            val cp = canonizer(p)
            checkCouldHaveClass(cp, cs: _*)            
        }
    }

    def checkWPathWf(wp: ir.WcPath): Unit =
        wp match {
            case ir.WcReadableBy(ps) =>
                checkCanonsAndCouldHaveClass(canonPath, ps, ir.c_interval)
                
            case ir.WcWritableBy(ps) =>
                checkCanonsAndCouldHaveClass(canonPath, ps, ir.c_interval)
                
            case ir.WcHbNow(ps) =>
                checkCanonsAndCouldHaveClass(canonPath, ps, ir.c_interval)
                
            case p: ir.Path =>
                env.canonPath(p)
        }
        
    def checkLengths(l1: List[_], l2: List[_], msg: String) = preservesEnv {
        if(l1.length != l2.length)
            throw new CheckFailure(msg, l1.length, l2.length)
    }
    
    def checkWghostsWf(wt: ir.WcClassType) {
        // Note: we don't check that the arguments
        // match the various ghost bounds etc.  We just
        // check that when the type is constructed.
        
        wt.wghosts.foldLeft(List[ir.FieldName]()) {
            case (l, wg) if l.contains(wg.f) => throw new CheckFailure("intervals.duplicate.ghost", wg.f)
            case (l, wg) => wg.f :: l
        }

        wt.wghosts.map(_.wp).foreach(checkWPathWf)

        // Every ghost should be for some unbound ghost field:
        val gfds_unbound = env.unboundGhostFieldsOnClassAndSuperclasses(wt.c)
        val expectedFieldNames: Set[ir.FieldName] = gfds_unbound.map(_.name) + ir.f_objCtor
        wt.wghosts.find(wg => !expectedFieldNames(wg.f)).foreach { wg =>
            throw new CheckFailure("intervals.no.such.ghost", wt.c, wg.f)
        }
    }
    
    def checkWtargWf(wta: ir.WcTypeArg) = wta match {
        case ir.BoundedTypeArg(tv, bounds) =>
            bounds.wts_lb.foreach(checkWtrefWf)
            bounds.wts_ub.foreach(checkWtrefWf)
            
        case ir.TypeArg(tv, wt) =>
            checkWtrefWf(wt)
    }
    
    def checkWtargsWf(wt: ir.WcClassType) {
        wt.wtargs.foldLeft(List[ir.TypeVarName]()) {
            case (l, wta) if l.contains(wta.tv) => throw new CheckFailure("intervals.duplicate.type.var", wta.tv)
            case (l, wta) => wta.tv :: l            
        }
        
        wt.wtargs.foreach(checkWtargWf)
        
        // Every type arg should be for some unbound type variable:
        val tvds_unbound = env.unboundTypeVarsDeclaredOnClassAndSuperclasses(wt.c)
        val expectedTvNames = tvds_unbound.map(_.name)
        wt.wtargs.find(wta => !expectedTvNames(wta.tv)).foreach { wta =>
            throw new CheckFailure("intervals.no.such.type.var", wt.c, wta.tv)            
        }
    }
    
    def checkWtrefWf(wt: ir.WcTypeRef) {
        wt match {
            case pt: ir.PathType =>
                val cp = env.reifiedPath(pt.p)
                val tvds = env.typeVarsDeclaredOnPath(cp)
                tvds.find(_.isNamed(pt.tv)) match {
                    case None => throw new CheckFailure("intervals.no.such.type.var", pt.p, pt.tv)
                    case Some(_) =>
                }
            
            case wt: ir.WcClassType =>
                checkWghostsWf(wt)
                checkWtargsWf(wt)
        }
    }
    
    def checkCall(lv_rcvr: ir.VarName, md: ir.MethodDecl, lvs_args: List[ir.VarName]) {
        env.reifiedLvs(lv_rcvr :: lvs_args)
        checkLengths(md.args, lvs_args, "intervals.wrong.number.method.arguments")        
    }
    
    def checkBranch(i: Int, stmts_stack: List[ir.StmtCompound], lvs: List[ir.VarName]) {
        env.reifiedLvs(lvs)
        if(i >= stmts_stack.length)
            throw new CheckFailure("intervals.invalid.stack.index", i, stmts_stack.length)
    }

    def checkStatement(stmts_stack: List[ir.StmtCompound])(stmt: ir.Stmt): Unit =
        at(stmt, ()) {
            stmt match {                  
                case ir.StmtSuperCtor(m, lvs_args) =>
                    val md = env.ctorOfClass(env.c_super, m)
                    checkCall(ir.lv_this, md, lvs_args)
                    
                case ir.StmtGetStatic(lv_def, c) =>
                    val cp = env.immutableCanonPath(ir.PathStatic(c))
                    setEnv(env.addPerm(lv_def, cp))
                
                case ir.StmtGetField(lv_def, lv_owner, f) =>
                    val cp_owner = env.reifiedLv(lv_owner)
                    val (_, rfd) = env.substdReifiedFieldDecl(cp_owner, f) 
                    addReifiedLocal(lv_def, rfd.wt)
                    
                case ir.StmtSetField(lv_owner, f, lv_value) =>
                    env.reifiedLv(lv_value)
                    
                    val cp_owner = env.reifiedLv(lv_owner)
                    env.substdReifiedFieldDecl(cp_owner, f) 

                case ir.StmtCheckType(lv, wt) =>
                    env.reifiedLv(lv)
                    checkWtrefWf(wt)

                case ir.StmtCall(lv_def, lv_rcvr, m, lvs_args) =>
                    val cp_rcvr = env.reifiedLv(lv_rcvr)                
                    val md = env.reqdMethod(env.methodDeclOfCp(cp_rcvr, m), m)
                    checkCall(lv_rcvr, md, lvs_args)
                    val msig = md.msig(env.lv_cur, lv_rcvr, lvs_args)
                    addReifiedLocal(lv_def, msig.wt_ret)
        
                case ir.StmtSuperCall(lv_def, m, lvs_args) =>
                    val md = env.reqdMethod(env.methodDeclOfClass(env.c_super, m), m)
                    checkCall(ir.lv_this, md, lvs_args)
                    val msig = md.msig(env.lv_cur, ir.lv_this, lvs_args)
                    addReifiedLocal(lv_def, msig.wt_ret)
                
                case ir.StmtNew(lv_def, ct, m, lvs_args) =>
                    if(env.classDecl(ct.c).attrs.interface)
                        throw new CheckFailure("intervals.new.interface", ct.c)
                        
                    // XXX currently you cannot specify an explicit
                    //     objCtor for interval subtypes.  This is 
                    //     needed so that we can assume that this.Constructor hb this,
                    //     which is basically always true.  We should however find
                    //     a less restrictive check (see TODO.sp for more details).
                    if(env.isSubclass(ct, ir.c_interval)) {
                        ct.ghosts.find(_.isNamed(ir.f_objCtor)).foreach { g =>
                            throw new CheckFailure("intervals.explicit.objCtor.on.interval", g.p)
                        }
                    }
                        
                    checkWtrefWf(ct)
                    env.canonPaths(ct.ghosts.map(_.p))
                    val cps_args = env.reifiedLvs(lvs_args)
                    
                    // Check that all ghosts on the type C being instantiated are given a value:
                    val gfds_unbound = env.unboundGhostFieldsOnClassAndSuperclasses(ct.c)
                    gfds_unbound.find(gfd => ct.ghosts.find(_.isNamed(gfd.name)).isEmpty) match {
                        case Some(gfd) => throw new CheckFailure("intervals.no.value.for.ghost", gfd.name)
                        case None =>
                    }
                    
                    // Check that all type vars on the type C being instantiated are given a value:
                    val tvds_unbound = env.unboundTypeVarsDeclaredOnClassAndSuperclasses(ct.c)
                    tvds_unbound.find(tvd => ct.targs.find(_.isNamed(tvd.name)).isEmpty) match {
                        case Some(tvd) => throw new CheckFailure("intervals.no.value.for.type.var", tvd.name)
                        case None =>
                    }
                    
                    addReifiedLocal(lv_def, ct)
                    val md = env.ctorOfClass(ct.c, m)
                    checkCall(lv_def, md, lvs_args)                    
                    
                case ir.StmtCast(lv_def, wt, lv) => 
                    env.reifiedLv(lv)
                    checkWtrefWf(wt)
                    addReifiedLocal(lv_def, wt)
                    
                case ir.StmtNull(lv_def, wt) => 
                    checkWtrefWf(wt)
                    addReifiedLocal(lv_def, wt)
                    
                case ir.StmtReturn(olv) =>
                    olv.foreach(lv => env.reifiedLv(lv))
                    
                case ir.StmtHb(lv_from, lv_to) =>                
                    checkCanonAndSubclass(env.reifiedLv _, lv_from, ir.c_point, ir.c_interval)
                    checkCanonAndSubclass(env.reifiedLv _, lv_to, ir.c_point, ir.c_interval)
                    
                case ir.StmtLocks(lv_inter, lv_lock) =>
                    checkCanonAndSubclass(env.reifiedLv _, lv_inter, ir.c_interval)
                    checkCanonAndSubclass(env.reifiedLv _, lv_lock, ir.c_lock)
                    
                case ir.StmtBreak(i, lvs) =>
                    checkBranch(i, stmts_stack, lvs)
                    val stmt_compound = stmts_stack(i)
                    checkLengths(stmt_compound.defines, lvs, "intervals.incorrect.number.of.branch.args")

                case ir.StmtCondBreak(i, lvs) =>
                    checkBranch(i, stmts_stack, lvs)
                    val stmt_compound = stmts_stack(i)
                    checkLengths(stmt_compound.defines, lvs, "intervals.incorrect.number.of.branch.args")
                    
                case ir.StmtContinue(i, lvs) =>
                    checkBranch(i, stmts_stack, lvs)
                    val stmt_compound = stmts_stack(i)
                    stmt_compound.kind match {
                        case ir.Loop(args, _, _) =>
                            checkLengths(args, lvs, "intervals.incorrect.number.of.branch.args")
                            
                        case kind =>
                            throw new CheckFailure("intervals.continue.to.nonloop", kind)
                    }

                case stmt_c: ir.StmtCompound =>
                    val new_stack = stmt_c :: stmts_stack
                    stmt_c.kind match {
                        case ir.Block(seq) =>
                            savingEnv { checkStatementSeq(new_stack)(seq) }
                        case ir.Switch(seqs) =>
                            savingEnv { seqs.foreach(checkStatementSeq(new_stack)) }
                        case ir.Loop(args, lvs_initial, seq) =>
                            env.reifiedLvs(lvs_initial)
                            savingEnv {
                                addCheckedArgs(args)
                                checkStatementSeq(new_stack)(seq)
                            }
                        case ir.InlineInterval(x, init, run) =>
                            addReifiedLocal(x, ir.wt_constructedInterval)
                            savingEnv { 
                                checkStatementSeq(new_stack)(init) 
                                checkStatementSeq(new_stack)(run)
                            }
                        case ir.TryCatch(t, c) =>
                            savingEnv { checkStatementSeq(new_stack)(t) }
                            savingEnv { checkStatementSeq(new_stack)(c) }
                    }
                    
                    addCheckedArgs(stmt_c.defines)
            }        
        }
        
    def checkStatementSeq(stmts_stack: List[ir.StmtCompound])(seq: ir.StmtSeq) {
        seq.stmts.foreach(checkStatement(stmts_stack))
    }
    
    def addCheckedArgs(args: List[ir.LvDecl]) {
        args.flatMap(_.wps_identity).foreach(checkWPathWf)
        args.foreach { arg =>
            setEnv(env.addArg(arg))
            checkWtrefWf(arg.wt) 
        }
    }
    
    def checkReq(req: ir.Req) =
        at(req, ()) {
            req match {
                case ir.ReqWritableBy(ps, qs) =>
                    checkCanonsAndCouldHaveClass(canonPath, ps, ir.c_guard)
                    checkCanonsAndCouldHaveClass(canonPath, qs, ir.c_interval)
                case ir.ReqReadableBy(ps, qs) => 
                    checkCanonsAndCouldHaveClass(canonPath, ps, ir.c_guard)
                    checkCanonsAndCouldHaveClass(canonPath, qs, ir.c_interval)
                case ir.ReqHb(ps, qs) => 
                    checkCanonsAndCouldHaveClass(canonPath, ps, ir.c_point, ir.c_interval)
                    checkCanonsAndCouldHaveClass(canonPath, qs, ir.c_point, ir.c_interval)
                case ir.ReqSuspends(ps, qs) => 
                    checkCanonsAndCouldHaveClass(canonPath, ps, ir.c_interval)
                    checkCanonsAndCouldHaveClass(canonPath, qs, ir.c_interval)
            }   
        }
    
    def checkNoninterfaceMethodDecl(
        cd: ir.ClassDecl,          // class in which the method is declared
        md: ir.MethodDecl          // method to check
    ) = 
        at(md, ()) {
            savingEnv {
                // Define special vars "method" and "this":
                addGhostLocal(ir.lv_mthd, ir.wt_constructedInterval)

                setCurrent(ir.lv_mthd)
                addCheckedArgs(md.args)
                checkWtrefWf(md.wt_ret)                
                md.reqs.foreach(checkReq)
                checkStatementSeq(List())(md.body)
            }         
        }
        
    def checkNoninterfaceConstructorDecl(cd: ir.ClassDecl, md: ir.MethodDecl): Unit = 
        at(md, ()) {
            savingEnv {
                // Define special vars "method" (== this.constructor) and "this":
                val cp_ctor = env.immutableCanonPath(ir.ClassCtorFieldName(cd.name).thisPath)
                addPerm(ir.lv_mthd, cp_ctor)
                setCurrent(ir.lv_mthd)
                
                addCheckedArgs(md.args)
                md.reqs.foreach(checkReq)
        
                // TODO -- Have the checking of super() verify that p_this is a ghost
                // and reify it, and thus permit control-flow.  We then have to propagate
                // p_this between blocks though (yuck).
        
                checkStatementSeq(List())(md.body)
            }          
        }
        
    def checkFieldNameNotShadowed(
        env: TcEnv,
        decl: ({ def name: ir.FieldName })
    ) = {
        log.indented("checkFieldNameNotShadowed(%s)", decl) {
            val f = decl.name
            env.classAndSuperclasses(env.c_this).foreach { c =>
                log.indented("class(%s)", c) {
                    env.classDecl(c).ghostFieldDecls.filter(_.isNamed(f)).foreach { gfd =>
                        log("gfd: %s (eq? %s)", gfd, gfd eq decl)
                        if(gfd ne decl)
                            throw new CheckFailure("intervals.shadowed.field", c, f)                
                    }
                    env.classDecl(c).reifiedFieldDecls.filter(_.isNamed(f)).foreach { rfd =>
                        log("rfd: %s (eq? %s)", rfd, rfd eq decl)
                        if(rfd ne decl)
                            throw new CheckFailure("intervals.shadowed.field", c, f)                
                    }
                }
            }                    
        }
    }
    
    def checkReifiedFieldDecl(cd: ir.ClassDecl, rfd: ir.ReifiedFieldDecl): Unit = {
        at(rfd, ()) {
            savingEnv {
                checkFieldNameNotShadowed(env, rfd)
                checkWtrefWf(rfd.wt)
                env.canonPath(rfd.p_guard)
            }
        }        
    }
    
    def checkGhostFieldDecl(cd: ir.ClassDecl, gfd: ir.GhostFieldDecl): Unit = {
        at(gfd, ()) {
            savingEnv {
                checkFieldNameNotShadowed(env, gfd)
                env.classDecl(gfd.c)
            }
        }        
    }
        
    def checkTypeVarDecl(cd: ir.ClassDecl, tvd: ir.TypeVarDecl): Unit = {
        at(tvd, ()) {
            savingEnv {
                // Check that type vars are not shadowed:
                env.classAndSuperclasses(cd.name).foreach { c =>
                    env.classDecl(c).typeVarDecls.filter(_.isNamed(tvd.name)).foreach { tvd1 =>
                        if(tvd ne tvd1)
                            throw new CheckFailure("intervals.shadowed.type.var", c, tvd.name)                        
                    }
                }
                
                tvd.wts_lb.foreach(checkWtrefWf)
            }
        }        
    }
        
    // ___ Classes and interfaces ___________________________________________
    
    def checkIsInterface(c: ir.ClassName) {
        val cd = env.classDecl(c)
        if(!cd.attrs.interface) 
            throw new CheckFailure("intervals.superType.not.interface", c)
    }
    
    def checkInterfaceSuperclass(c: ir.ClassName) {
        val cd = env.classDecl(c)
        if(c != ir.c_object) checkIsInterface(c)
    }
    
    def checkInterfaceConstructorDecl(cd: ir.ClassDecl, md: ir.MethodDecl) = {
        at(md, ()) {
            // This doesn't quite work: goal is just to verify that interface ctor's
            // do not work.
            //if(md != ir.md_emptyCtor)
            //    throw new CheckFailure("intervals.invalid.ctor.in.interface")
        }        
    }
    
    def checkInterfaceMethodDecl(cd: ir.ClassDecl, md: ir.MethodDecl) = {
        checkNoninterfaceMethodDecl(cd, md)        
    }
    
    def checkInterfaceClassDecl(cd: ir.ClassDecl) = {
        at(cd, ()) {
            savingEnv {
                // TODO Is this everything?
                setEnv(env.withThisClass(cd.name))
                cd.superClasses.foreach(checkInterfaceSuperclass)
                if(!cd.reifiedFieldDecls.isEmpty)
                    throw new CheckFailure("intervals.interface.with.fields")
                cd.ctors.foreach(checkInterfaceConstructorDecl(cd, _))
                cd.methods.foreach(checkInterfaceMethodDecl(cd, _))
            }
        }        
    }
        
    def checkIsNotInterface(c: ir.ClassName) {
        val cd_super = env.classDecl(c)
        if(cd_super.attrs.interface) 
            throw new CheckFailure("intervals.superType.interface", c)
    }

    def checkNoninterfaceClassDecl(cd: ir.ClassDecl) = 
        at(cd, ()) {
            savingEnv {
                setEnv(env.withThisClass(cd.name))
                cd.superClasses.take(1).foreach(checkIsNotInterface)
                cd.superClasses.drop(1).foreach(checkIsInterface)
                cd.ghostFieldDecls.foreach(checkGhostFieldDecl(cd, _))
                cd.reifiedFieldDecls.foreach(checkReifiedFieldDecl(cd, _))
                cd.ctors.foreach(checkNoninterfaceConstructorDecl(cd, _))
                cd.methods.foreach(checkNoninterfaceMethodDecl(cd, _))                    
            }
        }
        
    def checkClassDecl(cd: ir.ClassDecl) = log.indented(cd) {        
        if(cd.attrs.interface) checkInterfaceClassDecl(cd)
        else checkNoninterfaceClassDecl(cd)
    }        
}