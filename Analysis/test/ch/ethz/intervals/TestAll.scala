package ch.ethz.intervals

import java.io.File

object TestAll {
    val DEBUG_DIR = new File("../debug-logs")
    
    // These substitutions are performed.  They are not needed in program
    // text, but are useful in the expected error messages:
    val substs = List(
        ("#Constructor", ir.f_objCtor),
        ("#Creator", ir.f_creator),
        ("#Parent", ir.f_parent),
        ("#Object", ir.c_object),
        ("#Interval", ir.c_interval),
        ("#Guard", ir.c_guard),
        ("#Point", ir.c_point),
        ("#Lock", ir.c_lock),
        ("#String", ir.c_string),
        ("#RacyGuard", ir.c_RacyGuard),
        ("#void", ir.c_void)
    )
    
    def subst(text0: String) = {
        substs.foldLeft(text0) { case (t, (a, b)) => t.replace(a, b.toString) }        
    }
    
}