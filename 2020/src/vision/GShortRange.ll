//
// Inputs
//
Inputs
{
	Mat source0;
}

//
// Variables
//
Outputs
{
	Mat hsvThresholdOutput;
	ContoursReport findContoursOutput;
	ContoursReport convexHullsOutput;
	ContoursReport filterContoursOutput;
}

//
// Steps
//

Step HSV_Threshold0
{
    Mat hsvThresholdInput = source0;
    List hsvThresholdHue = [51.798561151079134, 66.36363636363636];
    List hsvThresholdSaturation = [208.6780575539568, 255.0];
    List hsvThresholdValue = [146.76258992805754, 255.0];

    hsvThreshold(hsvThresholdInput, hsvThresholdHue, hsvThresholdSaturation, hsvThresholdValue, hsvThresholdOutput);
}

Step Find_Contours0
{
    Mat findContoursInput = hsvThresholdOutput;
    Boolean findContoursExternalOnly = false;

    findContours(findContoursInput, findContoursExternalOnly, findContoursOutput);
}

Step Convex_Hulls0
{
    ContoursReport convexHullsContours = findContoursOutput;

    convexHulls(convexHullsContours, convexHullsOutput);
}

Step Filter_Contours0
{
    ContoursReport filterContoursContours = convexHullsOutput;
    Double filterContoursMinArea = 0.0;
    Double filterContoursMinPerimeter = 0.0;
    Double filterContoursMinWidth = 25.0;
    Double filterContoursMaxWidth = 100.0;
    Double filterContoursMinHeight = 10.0;
    Double filterContoursMaxHeight = 40.0;
    List filterContoursSolidity = [84.53237410071944, 100.0];
    Double filterContoursMaxVertices = 30.0;
    Double filterContoursMinVertices = 3.0;
    Double filterContoursMinRatio = 1.5;
    Double filterContoursMaxRatio = 3.0;

    filterContours(filterContoursContours, filterContoursMinArea, filterContoursMinPerimeter, filterContoursMinWidth, filterContoursMaxWidth, filterContoursMinHeight, filterContoursMaxHeight, filterContoursSolidity, filterContoursMaxVertices, filterContoursMinVertices, filterContoursMinRatio, filterContoursMaxRatio, filterContoursOutput);
}




