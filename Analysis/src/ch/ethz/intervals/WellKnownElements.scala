package ch.ethz.intervals

import scala.collection.JavaConversions._

import javax.lang.model.element.ExecutableElement
import javax.lang.model.util.Elements
import javax.lang.model.util.{ElementFilter => EF}
import javax.lang.model.util.Types

class WellKnownElements(elements: Elements, types: Types) {
    class TypeInfo[C](val cls: Class[C]) {
        val elem = elements.getTypeElement(cls.getName)
        val name = elem.getQualifiedName
        val ty = elem.asType
        
        def field(fname: String) = {
            EF.fieldsIn(elem.getEnclosedElements).find(mem =>
                mem.getSimpleName.contentEquals(fname)
            ).get
        }
        
        def method(mname: String, argTypes: TypeInfo[_]*): ExecutableElement = {
            val argTypesList = argTypes.toList
            EF.methodsIn(elem.getEnclosedElements).find(mem =>
                mem.getSimpleName.contentEquals(mname) && 
                mem.getParameters.size == argTypes.length &&
                argTypesList.zip(mem.getParameters.toList).forall { case (exp, act) =>
                    types.isSameType(exp.ty, act.asType)
                }
            ).get
        }
    }

    val Intervals = new TypeInfo(classOf[Intervals])
    val Interval = new TypeInfo(classOf[Interval])
    val Point = new TypeInfo(classOf[Point])
    val Guard = new TypeInfo(classOf[ch.ethz.intervals.guard.Guard])
    val Object = new TypeInfo(classOf[Object])
    val Unconstructed = new TypeInfo(classOf[ch.ethz.intervals.quals.Unconstructed])
    val DefinesGhost = new TypeInfo(classOf[ch.ethz.intervals.quals.DefinesGhost])
    val Requires = new TypeInfo(classOf[ch.ethz.intervals.quals.Requires])
    
    val addHbIntervalInterval = Intervals.method("addHb", Interval, Interval)
    val addHbIntervalPoint = Intervals.method("addHb", Interval, Point)
    val addHbPointInterval = Intervals.method("addHb", Point, Interval)
    val addHbPointPoint = Intervals.method("addHb", Point, Point)
    val addHb = Set(addHbIntervalInterval, addHbIntervalPoint, addHbPointInterval, addHbPointPoint)
    
    val ofClass = DefinesGhost.method("ofClass")
}